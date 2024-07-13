package com.cherry.kmp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "dice.preferences_pb"

fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

class AppDatastore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val LANGUAGE = stringPreferencesKey("language")
        val ITEMS = stringPreferencesKey("items")
    }

    val language: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LANGUAGE]
    }

    suspend fun saveLanguage(value: String) {
        dataStore.edit { settings ->
            settings[LANGUAGE] = value
        }
    }

    val items: Flow<List<String>> =
        dataStore.data
            .map { preferences ->
                getLeaguesSettingFromString(preferences[ITEMS])
            }

    suspend fun updatesItems(leagues: List<String>) {
        dataStore.edit { preferences ->
            preferences[ITEMS] = leagues.joinToString(separator = ",")
        }
    }

    private fun getLeaguesSettingFromString(settingsString: String?) =
        settingsString?.split(",") ?: emptyList()
}