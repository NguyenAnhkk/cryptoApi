package com.example.cryptoapi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cryptoapi.data.FavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FavoritesRepository(application)

    val favorites: StateFlow<Set<String>> = repository.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    fun toggle(coinId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(coinId)
        }
    }
}


