package com.example.cryptoapi.data.models

data class PriceHistory(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)

data class PricePoint(
    val timestamp: Long,
    val price: Double
)
