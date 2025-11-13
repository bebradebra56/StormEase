package com.stromeese.appsofr.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.DailyStats
import com.stromeese.appsofr.data.repository.StormEaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class TimePeriod {
    DAY, WEEK, MONTH
}

data class CategoryData(
    val category: ActivityCategory,
    val minutes: Int,
    val percentage: Float
)

data class StatisticsUiState(
    val selectedPeriod: TimePeriod = TimePeriod.DAY,
    val categoryData: List<CategoryData> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val totalMinutes: Int = 0,
    val balanceScore: Float = 0f,
    val isBalanced: Boolean = false,
    val recommendation: String = "",
    val isLoading: Boolean = true
)

class StatisticsViewModel(
    private val repository: StormEaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics(TimePeriod.DAY)
    }

    fun selectPeriod(period: TimePeriod) {
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true) }
        loadStatistics(period)
    }

    private fun loadStatistics(period: TimePeriod) {
        viewModelScope.launch {
            val (startDate, endDate) = getDateRange(period)
            
            repository.getActivitiesBetweenDates(startDate, endDate).collect { activities ->
                var workMinutes = 0
                var restMinutes = 0
                var personalMinutes = 0

                activities.forEach { activity ->
                    when (activity.category) {
                        ActivityCategory.WORK -> workMinutes += activity.durationMinutes
                        ActivityCategory.REST -> restMinutes += activity.durationMinutes
                        ActivityCategory.PERSONAL -> personalMinutes += activity.durationMinutes
                    }
                }

                val totalMinutes = workMinutes + restMinutes + personalMinutes
                val isBalanced = workMinutes > 0 && restMinutes > 0 && personalMinutes > 0

                val categoryData = if (totalMinutes > 0) {
                    listOf(
                        CategoryData(
                            ActivityCategory.WORK,
                            workMinutes,
                            workMinutes.toFloat() / totalMinutes * 100f
                        ),
                        CategoryData(
                            ActivityCategory.REST,
                            restMinutes,
                            restMinutes.toFloat() / totalMinutes * 100f
                        ),
                        CategoryData(
                            ActivityCategory.PERSONAL,
                            personalMinutes,
                            personalMinutes.toFloat() / totalMinutes * 100f
                        )
                    )
                } else {
                    emptyList()
                }

                val balanceScore = calculateBalanceScore(workMinutes, restMinutes, personalMinutes)
                val recommendation = generateRecommendation(workMinutes, restMinutes, personalMinutes)

                _uiState.update {
                    it.copy(
                        categoryData = categoryData,
                        activities = activities,
                        totalMinutes = totalMinutes,
                        balanceScore = balanceScore,
                        isBalanced = isBalanced,
                        recommendation = recommendation,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun getDateRange(period: TimePeriod): Pair<String, String> {
        val endDate = StormEaseRepository.getCurrentDate()
        val startDate = when (period) {
            TimePeriod.DAY -> endDate
            TimePeriod.WEEK -> StormEaseRepository.getDateDaysAgo(6)
            TimePeriod.MONTH -> StormEaseRepository.getDateDaysAgo(29)
        }
        return Pair(startDate, endDate)
    }

    private fun calculateBalanceScore(work: Int, rest: Int, personal: Int): Float {
        val total = work + rest + personal
        if (total == 0 || work == 0 || rest == 0 || personal == 0) return 0f

        val ideal = total / 3f
        val workDiff = kotlin.math.abs(work - ideal)
        val restDiff = kotlin.math.abs(rest - ideal)
        val personalDiff = kotlin.math.abs(personal - ideal)
        val totalDiff = workDiff + restDiff + personalDiff
        val maxDiff = total * 2f / 3f

        return ((1f - (totalDiff / maxDiff)) * 100f).coerceIn(0f, 100f)
    }

    private fun generateRecommendation(work: Int, rest: Int, personal: Int): String {
        val total = work + rest + personal
        if (total == 0) return "Start logging activities to see your balance!"

        val workPercent = (work.toFloat() / total * 100).toInt()
        val restPercent = (rest.toFloat() / total * 100).toInt()
        val personalPercent = (personal.toFloat() / total * 100).toInt()

        return when {
            workPercent > 50 -> "You've spent $workPercent% on work. Time for some rest!"
            restPercent > 50 -> "You've spent $restPercent% resting. Consider being more productive!"
            personalPercent > 50 -> "You've spent $personalPercent% on personal matters. Great self-care!"
            work == 0 -> "No work logged. Consider adding some productive activities."
            rest == 0 -> "No rest logged. Remember to take breaks!"
            personal == 0 -> "No personal time logged. Don't forget about yourself!"
            else -> "Excellent balance! You're living like a true Olympian!"
        }
    }

    class Factory(private val repository: StormEaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                return StatisticsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

