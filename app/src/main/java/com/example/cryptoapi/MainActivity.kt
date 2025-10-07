package com.example.cryptoapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cryptoapi.ui.theme.CryptoApiTheme
import android.net.Uri
import com.example.cryptoapi.ui.view.ChartScreen
import com.example.cryptoapi.ui.view.PostListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoApiTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "coin_list"
                    ) {
                        composable("coin_list") {
                            PostListScreen(
                                paddingValues = paddingValues,
                                onCoinClick = { coinId, coinName ->
                                    val encodedName = Uri.encode(coinName)
                                    navController.navigate("chart/$coinId/$encodedName")
                                }
                            )
                        }
                        composable("chart/{coinId}/{coinName}") { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            val coinName = backStackEntry.arguments?.getString("coinName")?.let { Uri.decode(it) } ?: ""
                            ChartScreen(
                                coinId = coinId,
                                coinName = coinName,
                                paddingValues = paddingValues,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
