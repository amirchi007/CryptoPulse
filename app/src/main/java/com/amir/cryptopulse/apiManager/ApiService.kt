package com.amir.cryptopulse.apiManager

import com.amir.cryptopulse.apiManager.model.ChartData
import com.amir.cryptopulse.apiManager.model.CoinsData
import com.amir.cryptopulse.apiManager.model.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "popular"
    ): Call<NewsData>

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") to_symbol: String = "USD",
        @Query("limit") limit_data: Int = 10
    ): Call<CoinsData>

    @Headers(API_KEY)
    @GET("{period}")
    fun getCharData(
        @Path("period") period: String,
        @Query("fsym") fromSymbol: String,
        @Query("limit") Limit: Int,
        @Query("aggregate") aggregate: Int,
        @Query("tsym") toSymbol: String = "USD"
    ): Call<ChartData>
}
