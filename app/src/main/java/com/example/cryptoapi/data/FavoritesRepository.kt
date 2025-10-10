package com.example.cryptoapi.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {
    private val FAVORITES_KEY: Preferences.Key<Set<String>> = stringSetPreferencesKey("favorite_coin_ids")

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[FAVORITES_KEY] ?: emptySet()
    }

    suspend fun toggleFavorite(coinId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = if (coinId in current) current - coinId else current + coinId
        }
    }

    suspend fun setFavorite(coinId: String, isFavorite: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = if (isFavorite) current + coinId else current - coinId
        }
    }
}


