package com.example.cryptoapi.data.api

import com.example.cryptoapi.data.models.CoinMarket
import com.example.cryptoapi.data.models.PriceHistory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("price_change_percentage") priceChangePercentage: String = "24h"
    ): List<CoinMarket>

    @GET("coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @Path("id") coinId: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 30,
        @Query("interval") interval: String = "daily"
    ): PriceHistory
}