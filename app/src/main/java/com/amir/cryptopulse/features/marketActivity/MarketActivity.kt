package com.amir.cryptopulse.features.marketActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amir.cryptopulse.apiManager.ApiManager
import com.amir.cryptopulse.apiManager.model.CoinAboutData
import com.amir.cryptopulse.apiManager.model.CoinAboutItem
import com.amir.cryptopulse.apiManager.model.CoinsData
import com.amir.cryptopulse.databinding.ActivityMarketBinding
import com.amir.cryptopulse.features.coinActivity.CoinActivity
import com.google.gson.Gson

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {

    private lateinit var binding: ActivityMarketBinding
    private lateinit var dataNews: ArrayList<Pair<String, String>>
    private lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>
    private val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        getAboutDataFromAssets()
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun setupToolbar() {
        binding.layoutToolbar.toolbar.title = "Crypto Market"
    }

    private fun setupListeners() {
        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            openUrl("https://www.livecoinwatch.com/")
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            initUi()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun initUi() {
        getNewsFromApi()
        getTopCoinsFromApi()
    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallBack<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()
            }

            override fun onError(errorMessage: String) {
                showToast("error -> $errorMessage")
            }
        })
    }

    private fun refreshNews() {
        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = dataNews[randomAccess].first
        binding.layoutNews.imgNews.setOnClickListener {
            openUrl(dataNews[randomAccess].second)
        }
        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews()
        }
    }

    private fun cleanData(data: List<CoinsData.Data>): List<CoinsData.Data> {
        return data.filter { it.rAW != null && it.dISPLAY != null }
    }

    private fun getTopCoinsFromApi() {
        apiManager.getConinsList(object : ApiManager.ApiCallBack<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(cleanData(data))
            }

            override fun onError(errorMessage: String) {
                showToast("error -> $errorMessage")
                Log.v("MarketActivity", errorMessage)
            }
        })
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        val marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
    }

    private fun getAboutDataFromAssets() {
        val fileInString = assets.open("currencyinfo.json").bufferedReader().use { it.readText() }
        aboutDataMap = mutableMapOf()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString, CoinAboutData::class.java)
        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCoinItemClicked(dataCoin: CoinsData.Data) {
        val intent = Intent(this, CoinActivity::class.java).apply {
            val bundle = Bundle().apply {
                putParcelable("bundle1", dataCoin)
                putParcelable("bundle2", aboutDataMap[dataCoin.coinInfo.name])
            }
            putExtra("bundle", bundle)
        }
        startActivity(intent)
    }
}
