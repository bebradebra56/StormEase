package com.stromeese.appsofr.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.DailyStats
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val todayStats: DailyStats? = null,
    val recentActivities: List<Activity> = emptyList(),
    val currentDate: String = "",
    val showLightning: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayData()
    }

    private fun loadTodayData() {
        val today = StormEaseRepository.getCurrentDate()
        _uiState.update { it.copy(currentDate = today, isLoading = true) }

        viewModelScope.launch {
            combine(
                repository.getDailyStatsByDate(today),
                repository.getActivitiesByDate(today)
            ) { stats, activities ->
                _uiState.update {
                    it.copy(
                        todayStats = stats,
                        recentActivities = activities.take(5),
                        isLoading = false,
                        showLightning = stats?.isBalanced == true && stats.balanceScore >= 90f
                    )
                }
            }.collect()
        }
    }

    fun refreshData() {
        loadTodayData()
    }

    fun dismissLightning() {
        _uiState.update { it.copy(showLightning = false) }
    }

    class Factory(private val repository: StormEaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

