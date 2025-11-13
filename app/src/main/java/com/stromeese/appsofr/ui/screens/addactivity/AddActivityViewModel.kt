package com.stromeese.appsofr.ui.screens.addactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.Emotion
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddActivityUiState(
    val activityName: String = "",
    val selectedCategory: ActivityCategory = ActivityCategory.WORK,
    val durationMinutes: Int = 30,
    val selectedEmotion: Emotion = Emotion.NEUTRAL,
    val isSaving: Boolean = false,
    val showSuccessAnimation: Boolean = false,
    val errorMessage: String? = null
)

class AddActivityViewModel(
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddActivityUiState())
    val uiState: StateFlow<AddActivityUiState> = _uiState.asStateFlow()

    fun updateActivityName(name: String) {
        _uiState.update { it.copy(activityName = name, errorMessage = null) }
    }

    fun updateCategory(category: ActivityCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateDuration(minutes: Int) {
        _uiState.update { it.copy(durationMinutes = minutes.coerceIn(1, 1440)) }
    }

    fun updateEmotion(emotion: Emotion) {
        _uiState.update { it.copy(selectedEmotion = emotion) }
    }

    fun saveActivity(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.activityName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Activity name cannot be empty") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val activity = Activity(
                    name = state.activityName.trim(),
                    category = state.selectedCategory,
                    durationMinutes = state.durationMinutes,
                    emotion = state.selectedEmotion,
                    date = StormEaseRepository.getCurrentDate()
                )
                
                repository.addActivity(activity)
                
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        showSuccessAnimation = true
                    )
                }
                
                // Small delay for animation
                kotlinx.coroutines.delay(500)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save activity: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetForm() {
        _uiState.value = AddActivityUiState()
    }

    class Factory(private val repository: StormEaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddActivityViewModel::class.java)) {
                return AddActivityViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

