package com.stromeese.appsofr.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.DailyStats
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ArchiveUiState(
    val dailyStats: List<DailyStats> = emptyList(),
    val selectedDate: String? = null,
    val selectedDayActivities: List<Activity> = emptyList(),
    val isLoading: Boolean = true
)

class ArchiveViewModel(
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        loadArchive()
    }

    private fun loadArchive() {
        viewModelScope.launch {
            repository.getAllDailyStats().collect { stats ->
                _uiState.value = _uiState.value.copy(
                    dailyStats = stats,
                    isLoading = false
                )
            }
        }
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        viewModelScope.launch {
            repository.getActivitiesByDate(date).collect { activities ->
                _uiState.value = _uiState.value.copy(
                    selectedDayActivities = activities
                )
            }
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedDate = null,
            selectedDayActivities = emptyList()
        )
    }

    class Factory(private val repository: StormEaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArchiveViewModel::class.java)) {
                return ArchiveViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

