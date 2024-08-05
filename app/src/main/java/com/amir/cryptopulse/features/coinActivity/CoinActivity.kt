package com.amir.cryptopulse.features.coinActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amir.cryptopulse.R
import com.amir.cryptopulse.apiManager.ALL
import com.amir.cryptopulse.apiManager.ApiManager
import com.amir.cryptopulse.apiManager.BASE_URL
import com.amir.cryptopulse.apiManager.HOUR
import com.amir.cryptopulse.apiManager.HOURS24
import com.amir.cryptopulse.apiManager.MONTH
import com.amir.cryptopulse.apiManager.MONTH3
import com.amir.cryptopulse.apiManager.WEEK
import com.amir.cryptopulse.apiManager.YEAR
import com.amir.cryptopulse.apiManager.model.ChartData
import com.amir.cryptopulse.apiManager.model.CoinAboutItem
import com.amir.cryptopulse.apiManager.model.CoinsData
import com.amir.cryptopulse.databinding.ActivityCoinBinding

class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    private lateinit var dataThisCoin: CoinsData.Data
    private lateinit var dataThisCoinAbout: CoinAboutItem
    val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fromIntent = intent.getBundleExtra("bundle")!!
        dataThisCoin = fromIntent.getParcelable<CoinsData.Data>("bundle1")!!

        if (fromIntent.getParcelable<CoinAboutItem>("bundle2") != null) {
            dataThisCoinAbout = fromIntent.getParcelable<CoinAboutItem>("bundle2")!!
        } else {
            dataThisCoinAbout = CoinAboutItem()
        }

        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName
        initUi()
    }

    private fun initUi() {
        initChartUi()
        initStatisticsUi()
        initAboutUi()
    }

    private fun initAboutUi() {
        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGithub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.coinRedit
        binding.layoutAbout.txtTwitter.text = "@    " + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc

        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteThisCoin(dataThisCoinAbout.coinWebsite!!)
        }
        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteThisCoin(dataThisCoinAbout.coinGithub!!)
        }
        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteThisCoin(dataThisCoinAbout.coinRedit!!)
        }
        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteThisCoin(BASE_URL + dataThisCoinAbout.coinWebsite!!)
        }
    }

    private fun openWebsiteThisCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun initStatisticsUi() {
        binding.layoutStatistics.tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvTodaysHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvTodayLowAmount.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvAlgorithm.text = dataThisCoin.coinInfo.algorithm
        binding.layoutStatistics.tvTotalVolume.text = dataThisCoin.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistics.tvAvgMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.tvSupplyNumber.text = dataThisCoin.dISPLAY.uSD.sUPPLY
    }

    private fun initChartUi() {
        var period: String = HOUR
        requestAndShowChart(period)
        binding.layoutChart.radioGroupMain.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio12h -> {
                    period = HOUR
                }

                R.id.radio1d -> {
                    period = HOURS24
                }

                R.id.radio1w -> {
                    period = WEEK
                }

                R.id.radio1m -> {
                    period = MONTH
                }

                R.id.radio3m -> {
                    period = MONTH3
                }

                R.id.radio1y -> {
                    period = YEAR
                }

                R.id.radioAll -> {
                    period = ALL
                }
            }
            requestAndShowChart(period)
        }

        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE

        if (dataThisCoin.coinInfo.fullName == "BUSD") {
            binding.layoutChart.txtCartChange2.text = "0%"
        } else {
            binding.layoutChart.txtCartChange2.text =
                dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }

        binding.layoutChart.txtCartChange1.text = " " + dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        val taghir = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        when {
            taghir > 0 -> {
                binding.layoutChart.txtCartChange2.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.layoutChart.txtChartUpDown.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.layoutChart.txtChartUpDown.text = "▲"
                binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            }

            taghir < 0 -> {
                binding.layoutChart.txtCartChange2.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.layoutChart.txtChartUpDown.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.layoutChart.txtChartUpDown.text = "▼"
                binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            }
        }

        binding.layoutChart.sparkViewMain.setScrubListener {

            // show price kamel
            if ( it == null ) {
                binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
            } else {
                // show price this dot
                binding.layoutChart.txtChartPrice.text = "$ " + (it as ChartData.Data).close.toString()
            }

        }

    }

    fun requestAndShowChart(period: String) {

        apiManager.getChartData(
            dataThisCoin.coinInfo.name,
            period,
            object : ApiManager.ApiCallBack<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkViewMain.adapter = chartAdapter
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(
                        this@CoinActivity,
                        "error => " + errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

}