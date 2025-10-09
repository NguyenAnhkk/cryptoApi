package com.example.cryptoapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoapi.data.api.RetrofitInstance
import com.example.cryptoapi.data.models.CoinMarket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel: ViewModel()  {
    private val _coins = MutableLiveData<List<CoinMarket>>()
    val coins: LiveData<List<CoinMarket>> = _coins
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        fetchMarkets()
    }
    private fun fetchMarkets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getMarkets()
                }
                _coins.value = result
                println("Fetched ${result.size} coins successfully")
            } catch (e: Exception) {
                println("Error fetching coins: ${e.message}")
                e.printStackTrace()
                _coins.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}