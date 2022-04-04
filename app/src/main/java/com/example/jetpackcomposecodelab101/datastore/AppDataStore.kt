package com.example.jetpackcomposecodelab101.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AppDataStore {
    suspend fun incrementCounter()
    val exampleCounterFlow: Flow<Int>
}

class DefaultAppDataStore(private val dataStore: DataStore<Preferences>) : AppDataStore {
    private val counter = intPreferencesKey("example_counter")
    override val exampleCounterFlow: Flow<Int> = dataStore.data.map { preferences ->
            // No type safety.
            preferences[counter] ?: 0
        }
    override suspend fun incrementCounter() {
        dataStore.edit { settings ->
            val currentCounterValue = settings[counter] ?: 0
            settings[counter] = currentCounterValue + 1
        }
    }
}