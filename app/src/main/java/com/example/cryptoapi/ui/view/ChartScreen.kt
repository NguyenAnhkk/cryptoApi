package com.example.cryptoapi.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoapi.data.models.PricePoint
import com.example.cryptoapi.viewmodel.ChartViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.VerticalPosition
import com.patrykandpatrick.vico.core.formatter.DecimalFormatValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChartScreen(
    coinId: String,
    coinName: String,
    viewModel: ChartViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    val priceHistory by viewModel.priceHistory.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    // Fetch data when screen loads
    LaunchedEffect(coinId) {
        viewModel.fetchPriceHistory(coinId)
    }

    // Màu sắc theme tối
    val darkBackground = Color(0xFF121212)
    val cardBackground = Color(0xFF1E1E1E)
    val primaryColor = Color(0xFFBB86FC)
    val successColor = Color(0xFF4CAF50)
    val errorColor = Color(0xFFCF6679)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header với nút back và tên coin
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }

                Text(
                    text = coinName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(48.dp)) // For balance
            }

            // Card thông tin giá hiện tại
            if (priceHistory.isNotEmpty()) {
                val currentPrice = priceHistory.last().price
                val firstPrice = priceHistory.first().price
                val changePercent = ((currentPrice - firstPrice) / firstPrice) * 100
                val changeColor = if (changePercent >= 0) successColor else errorColor
                val changeIcon = if (changePercent >= 0) "↗" else "↘"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Current Price",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textSecondary
                            )
                        )

                        Text(
                            text = "$${String.format("%.2f", currentPrice)}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                fontSize = 32.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = changeIcon,
                                color = changeColor,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${if (changePercent >= 0) "+" else ""}${String.format("%.2f", changePercent)}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = changeColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "(30d)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Chart card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = primaryColor,
                                    strokeWidth = 3.dp
                                )
                                Text(
                                    text = "Loading chart...",
                                    color = textSecondary
                                )
                            }
                        }
                        error != null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "Failed to load data",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = textPrimary
                                    )
                                )
                                Text(
                                    text = error.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = textSecondary
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { viewModel.fetchPriceHistory(coinId) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Try Again",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        priceHistory.isNotEmpty() -> {
                            PriceChart(
                                priceHistory = priceHistory,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "No data available",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = textPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceChart(
    priceHistory: List<PricePoint>,
    modifier: Modifier = Modifier
) {
    // Clean data: keep only finite prices
    val cleanedPoints = priceHistory.filter { it.price.isFinite() }
    if (cleanedPoints.size < 2) {
        // Not enough points to render a chart safely
        androidx.compose.material3.Text(
            "Not enough data",
            color = Color(0xFFB3B3B3)
        )
        return
    }

    val chartModel = entryModelOf(
        *cleanedPoints.mapIndexed { index, pricePoint ->
            index.toFloat() to pricePoint.price.toFloat()
        }.toTypedArray()
    )

    Chart(
        chart = lineChart(
            lines = listOf(
                LineSpec(
                    lineColor = Color(0xFF2196F3).toArgb(),
                    lineThicknessDp = 3f,
                    point = null,
                    pointSizeDp = 0f,
                    dataLabelVerticalPosition = VerticalPosition.Top,
                    dataLabelValueFormatter = DecimalFormatValueFormatter(),
                )
            ),
            spacing = 0.dp,
        ),
        model = chartModel,
        startAxis = null,
        bottomAxis = null,
        modifier = modifier
    )
}