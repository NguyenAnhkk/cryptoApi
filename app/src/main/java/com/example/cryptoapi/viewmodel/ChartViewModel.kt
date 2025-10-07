package com.example.cryptoapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoapi.data.api.RetrofitInstance
import com.example.cryptoapi.data.models.PricePoint
import kotlinx.coroutines.launch

class ChartViewModel: ViewModel() {
    private val _priceHistory = MutableLiveData<List<PricePoint>>()
    val priceHistory: LiveData<List<PricePoint>> = _priceHistory
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun fetchPriceHistory(coinId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getPriceHistory(coinId)
                val pricePoints = response.prices.map { priceData ->
                    PricePoint(
                        timestamp = (priceData[0] / 1000).toLong(), // Convert to seconds
                        price = priceData[1]
                    )
                }
                _priceHistory.value = pricePoints
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
