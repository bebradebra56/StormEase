package com.stromeese.appsofr.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SOUND_TYPE = stringPreferencesKey("sound_type")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    val isVibrationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
        }

    val isSoundEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] ?: true
        }

    val soundType: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_TYPE] ?: "thunder"
        }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] ?: true
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setSoundType(type: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_TYPE] = type
        }
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] = false
        }
    }
}

enum class SoundType(val displayName: String) {
    THUNDER("Thunder"),
    WIND("Wind"),
    SILENT("Silent")
}

