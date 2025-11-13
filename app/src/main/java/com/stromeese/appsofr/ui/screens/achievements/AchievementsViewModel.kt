package com.stromeese.appsofr.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.model.Achievement
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val newlyUnlockedAchievement: Achievement? = null,
    val isLoading: Boolean = true
)

class AchievementsViewModel(
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            repository.getAllAchievements().collect { achievements ->
                _uiState.value = _uiState.value.copy(
                    achievements = achievements,
                    isLoading = false
                )
            }
        }
    }

    fun dismissNewAchievement() {
        _uiState.value = _uiState.value.copy(newlyUnlockedAchievement = null)
    }

    class Factory(private val repository: StormEaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
                return AchievementsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

