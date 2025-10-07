package com.example.cryptoapi.data.models

data class CoinMarket(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String?,
    val current_price: Double?,
    val market_cap: Long?,
    val price_change_percentage_24h: Double?
)
