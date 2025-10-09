package com.example.cryptoapi.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptoapi.data.models.CoinMarket
import com.example.cryptoapi.viewmodel.PostViewModel

@Composable
fun PostListScreen(
    viewModel: PostViewModel = viewModel(),
    paddingValues: PaddingValues,
    onCoinClick: (String, String) -> Unit = { _, _ -> }
) {
    val data by viewModel.coins.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    val darkBackground = Color(0xFF0F0F23)
    val cardBackground = Color(0xFF1A1A2E)
    val primaryColor = Color(0xFF6366F1)
    val successColor = Color(0xFF10B981)
    val errorColor = Color(0xFFEF4444)
    val textPrimary = Color(0xFFFFFFFF)
    val textTitle = Color(0xFF000000)
    val textSecondary = Color(0xFF94A3B8)
    val accentGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    )

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = primaryColor,
                strokeWidth = 3.dp
            )
            Text(
                text = "Loading cryptocurrencies...",
                color = textSecondary,
                modifier = Modifier.padding(top = 16.dp)
            )
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            Column {
                Text(
                    text = "Crypto Market",
                    color = textTitle,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
                Text(
                    text = "Real-time cryptocurrency prices",
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Top Gainers Section
            Column {
                Text(
                    text = "🔥 Top Gainers (24h)",
                    color = textTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(currencies24h) { coin ->
                        CoinChip(
                            coin = coin,
                            onClick = { onCoinClick(coin.id, coin.name) },
                            primaryColor = primaryColor,
                            successColor = successColor,
                            errorColor = errorColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardBackground = cardBackground
                        )
                    }
                }
            }

            // Top Losers Section
            Column {
                Text(
                    text = "📉 Top Losers (24h)",
                    color = textTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(topLosers) { coin ->
                        CoinChip(
                            coin = coin,
                            onClick = { onCoinClick(coin.id, coin.name) },
                            primaryColor = primaryColor,
                            successColor = successColor,
                            errorColor = errorColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardBackground = cardBackground
                        )
                    }
                }
            }

            // Top Currencies Section
            Column {
                Text(
                    text = "🏆 Top Currencies",
                    color = textTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(topCurrencies) { coin ->
                        CoinListItem(
                            coin = coin,
                            onClick = { onCoinClick(coin.id, coin.name) },
                            primaryColor = primaryColor,
                            successColor = successColor,
                            errorColor = errorColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardBackground = cardBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoinChip(
    coin: CoinMarket,
    onClick: () -> Unit,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBackground: Color
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = coin.symbol.uppercase(),
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                val change = coin.price_change_percentage_24h
                val changeText = change?.let { String.format("%.2f%%", it) } ?: "-"
                val changeColor = if ((change ?: 0.0) >= 0.0) successColor else errorColor
                Text(
                    text = changeText,
                    color = changeColor,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun CoinListItem(
    coin: CoinMarket,
    onClick: () -> Unit,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBackground: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin Image
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            // Coin Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = coin.name,
                            color = textPrimary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = coin.symbol.uppercase(),
                            color = textSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Price and Change
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        val price = coin.current_price?.let {
                            "$${String.format("%.2f", it)}"
                        } ?: "-"
                        Text(
                            text = price,
                            color = textPrimary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        val change = coin.price_change_percentage_24h
                        val changeText = change?.let {
                            "${if (it >= 0) "+" else ""}${String.format("%.2f%%", it)}"
                        } ?: "-"
                        val changeColor = if ((change ?: 0.0) >= 0.0) successColor else errorColor
                        Text(
                            text = changeText,
                            color = changeColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Market Cap
                val mcap = coin.market_cap?.let {
                    "MCap: $${String.format("%,d", it)}"
                } ?: "MCap: -"
                Text(
                    text = mcap,
                    color = textSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CoinCard(
    coin: CoinMarket,
    onClick: () -> Unit,
    primaryColor: Color = Color(0xFF6366F1),
    successColor: Color = Color(0xFF10B981),
    errorColor: Color = Color(0xFFEF4444),
    textPrimary: Color = Color(0xFFFFFFFF),
    textSecondary: Color = Color(0xFF94A3B8),
    cardBackground: Color = Color(0xFF1A1A2E)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = coin.name,
                    color = textPrimary,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = coin.symbol.uppercase(),
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
                val price = coin.current_price?.let {
                    "$${String.format("%.2f", it)}"
                } ?: "-"
                val change = coin.price_change_percentage_24h?.let {
                    "${if (it >= 0) "+" else ""}${String.format("%.2f%%", it)}"
                } ?: "-"
                Text(
                    text = "Price: $price",
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
                val changeColor = if ((coin.price_change_percentage_24h ?: 0.0) >= 0.0) successColor else errorColor
                Text(
                    text = "24h: $change",
                    color = changeColor,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}