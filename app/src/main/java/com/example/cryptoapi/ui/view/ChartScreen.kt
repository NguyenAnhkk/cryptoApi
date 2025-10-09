package com.example.cryptoapi.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlin.math.roundToInt
import com.example.cryptoapi.data.models.PricePoint
import com.example.cryptoapi.viewmodel.ChartViewModel
import com.example.cryptoapi.viewmodel.ChartRange
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
    var selectedRange by remember { mutableStateOf(ChartRange.D30) }
    LaunchedEffect(coinId, selectedRange) {
        viewModel.fetchPriceHistory(coinId, selectedRange)
    }

    // Modern color palette - Dark Theme
    val darkBackground = Color(0xFF0F0F23)
    val cardBackground = Color(0xFF1A1A2E)
    val primaryColor = Color(0xFF6366F1)
    val successColor = Color(0xFF10B981)
    val errorColor = Color(0xFFEF4444)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val accentGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
    )

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                val changeIcon = if (changePercent >= 0) "📈" else "📉"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                fontSize = 36.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = changeIcon,
                                color = changeColor,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${if (changePercent >= 0) "+" else ""}${
                                    String.format(
                                        "%.2f",
                                        changePercent
                                    )
                                }%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = changeColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "(${getRangeText(selectedRange)})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Range selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ChartRange.values().forEach { range ->
                        RangeChip(
                            label = getRangeLabel(range),
                            range = range,
                            isSelected = selectedRange == range,
                            onSelected = { selectedRange = it },
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            cardBackground = cardBackground
                        )
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
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
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
                                    text = "Loading chart data...",
                                    color = textSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        error != null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 52.sp
                                )
                                Text(
                                    text = "Failed to load data",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = textPrimary,
                                        fontWeight = FontWeight.SemiBold
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
                                    onClick = { viewModel.fetchPriceHistory(coinId, selectedRange) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = "Try Again",
                                        color = Color.White, // Đã sửa từ Black thành White
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        priceHistory.isNotEmpty() -> {
                            PriceChart(
                                priceHistory = priceHistory,
                                modifier = Modifier.fillMaxSize(),
                                primaryColor = primaryColor,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary
                            )
                        }

                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 52.sp
                                )
                                Text(
                                    text = "No data available",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Try selecting a different time range",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = textSecondary
                                    ),
                                    textAlign = TextAlign.Center
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
private fun RangeChip(
    label: String,
    range: ChartRange,
    isSelected: Boolean,
    onSelected: (ChartRange) -> Unit,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBackground: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelected(range) }
            .background(
                if (isSelected) primaryColor else cardBackground
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else textSecondary, // Đã sửa từ Black thành White
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun PriceChart(
    priceHistory: List<PricePoint>,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF6366F1),
    textPrimary: Color = Color(0xFFFFFFFF),
    textSecondary: Color = Color(0xFF94A3B8)
) {
    val cleanedPoints = priceHistory.filter { it.price.isFinite() && it.price > 0 }
    if (cleanedPoints.size < 2) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📈",
                fontSize = 48.sp
            )
            Text(
                text = "Not enough data points",
                color = textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
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
        startAxis = rememberStartAxis(
            valueFormatter = DecimalFormatAxisValueFormatter("#,###.#####"),
            guideline = null,
        ),
            bottomAxis = null,
        modifier = modifier
    )
}

private fun getRangeLabel(range: ChartRange): String {
    return when (range) {
        ChartRange.D1 -> "1D"
        ChartRange.D7 -> "7D"
        ChartRange.D30 -> "30D"
        ChartRange.ALL -> "ALL"
    }
}

private fun getRangeText(range: ChartRange): String {
    return when (range) {
        ChartRange.D1 -> "24H"
        ChartRange.D7 -> "7D"
        ChartRange.D30 -> "30D"
        ChartRange.ALL -> "ALL"
    }
}