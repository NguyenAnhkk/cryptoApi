package com.example.cryptoapi.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cryptoapi.data.models.CoinMarket
import com.example.cryptoapi.viewmodel.PostViewModel

@Composable
fun PostListScreen(
    viewModel: PostViewModel = PostViewModel(),
    paddingValues: PaddingValues,
    onCoinClick: (String, String) -> Unit = { _, _ -> }
) {
    val data by viewModel.coins.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val currencies24h = data
            .sortedByDescending { it.price_change_percentage_24h ?: 0.0 }
            .take(12)

        val topLosers = data
            .filter { (it.price_change_percentage_24h ?: 0.0) < 0.0 }
            .sortedBy { it.price_change_percentage_24h ?: 0.0 }
            .take(12)

        val topCurrencies = data
            .sortedByDescending { it.market_cap ?: 0L }
            .take(25)

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "24H Currencies",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(currencies24h) { coin ->
                    CoinChip(
                        coin = coin,
                        onClick = { onCoinClick(coin.id, coin.name) }
                    )
                }
            }

            Text(
                text = "Top Losers",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(topLosers) { coin ->
                    CoinChip(
                        coin = coin,
                        onClick = { onCoinClick(coin.id, coin.name) }
                    )
                }
            }

            Text(
                text = "Top Currencies",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topCurrencies) { coin ->
                    CoinListItem(
                        coin = coin,
                        onClick = { onCoinClick(coin.id, coin.name) }
                    )
                }
            }
        }

    }
}

@Composable
fun CoinCard(
    coin: CoinMarket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = coin.name, color = Color.Black)
            Text(text = coin.symbol.uppercase(), color = Color.Black)
            val price = coin.current_price?.let { "$" + String.format("%.2f", it) } ?: "-"
            val change =
                coin.price_change_percentage_24h?.let { String.format("%.2f%%", it) } ?: "-"
            Text(text = "Price: $price", color = Color.Black)
            Text(text = "24h: $change", color = Color.Black)
        }
    }
}

@Composable
private fun CoinChip(
    coin: CoinMarket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = coin.name, color = Color.Black)
            val change = coin.price_change_percentage_24h
            val changeText = change?.let { String.format("%.2f%%", it) } ?: "-"
            val changeColor = if ((change ?: 0.0) >= 0.0) Color(0xFF2E7D32) else Color(0xFFC62828)
            Text(text = changeText, color = changeColor)
        }
    }
}

@Composable
private fun CoinListItem(
    coin: CoinMarket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = coin.name, color = Color.Black)
            val price = coin.current_price?.let { "$" + String.format("%.2f", it) } ?: "-"
            val mcap = coin.market_cap?.let { String.format("%,d", it) } ?: "-"
            val change =
                coin.price_change_percentage_24h?.let { String.format("%.2f%%", it) } ?: "-"
            Text(text = "Price: $price", color = Color.Black)
            Text(text = "Market Cap: $mcap", color = Color.Black)
            Text(text = "24h: $change", color = Color.Black)
        }
    }
}