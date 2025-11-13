package com.stromeese.appsofr.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.preferences.SoundType
import com.stromeese.appsofr.data.preferences.UserPreferences
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val isVibrationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val selectedSoundType: String = "thunder",
    val showResetDialog: Boolean = false,
    val isResetting: Boolean = false
)

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            combine(
                userPreferences.isDarkTheme,
                userPreferences.isVibrationEnabled,
                userPreferences.isSoundEnabled,
                userPreferences.soundType
            ) { isDark, vibration, sound, soundType ->
                _uiState.value = _uiState.value.copy(
                    isDarkTheme = isDark,
                    isVibrationEnabled = vibration,
                    isSoundEnabled = sound,
                    selectedSoundType = soundType
                )
            }.collect()
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            userPreferences.setDarkTheme(!_uiState.value.isDarkTheme)
        }
    }

    fun toggleVibration() {
        viewModelScope.launch {
            userPreferences.setVibrationEnabled(!_uiState.value.isVibrationEnabled)
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            userPreferences.setSoundEnabled(!_uiState.value.isSoundEnabled)
        }
    }

    fun setSoundType(type: String) {
        viewModelScope.launch {
            userPreferences.setSoundType(type)
        }
    }

    fun showResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = true)
    }

    fun dismissResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = false)
    }

    fun resetAllData() {
        _uiState.value = _uiState.value.copy(isResetting = true)
        viewModelScope.launch {
            repository.resetAllData()
            _uiState.value = _uiState.value.copy(
                isResetting = false,
                showResetDialog = false
            )
        }
    }

    class Factory(
        private val userPreferences: UserPreferences,
        private val repository: StormEaseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(userPreferences, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

