package com.stromeese.appsofr.data.repository

import com.stromeese.appsofr.data.dao.ActivityDao
import com.stromeese.appsofr.data.dao.AchievementDao
import com.stromeese.appsofr.data.dao.DailyStatsDao
import com.stromeese.appsofr.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class StormEaseRepository(
    private val activityDao: ActivityDao,
    private val achievementDao: AchievementDao,
    private val dailyStatsDao: DailyStatsDao
) {
    // Activities
    fun getAllActivities(): Flow<List<Activity>> = activityDao.getAllActivities()
    
    fun getActivitiesByDate(date: String): Flow<List<Activity>> = 
        activityDao.getActivitiesByDate(date)
    
    fun getActivitiesBetweenDates(startDate: String, endDate: String): Flow<List<Activity>> =
        activityDao.getActivitiesBetweenDates(startDate, endDate)
    
    suspend fun addActivity(activity: Activity): Long {
        val id = activityDao.insertActivity(activity)
        updateDailyStats(activity.date)
        checkAndUpdateAchievements()
        return id
    }
    
    suspend fun deleteActivity(activity: Activity) {
        activityDao.deleteActivity(activity)
        updateDailyStats(activity.date)
    }
    
    suspend fun deleteAllActivities() {
        activityDao.deleteAllActivities()
        dailyStatsDao.deleteAllDailyStats()
    }

    // Achievements
    fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    
    fun getUnlockedAchievements(): Flow<List<Achievement>> = 
        achievementDao.getUnlockedAchievements()
    
    suspend fun initializeAchievements() {
        val existingAchievements = achievementDao.getAllAchievements().first()
        if (existingAchievements.isEmpty()) {
            val defaultAchievements = listOf(
                Achievement(
                    id = AchievementIds.DAY_OF_HARMONY,
                    name = "Day of Harmony",
                    description = "Achieve 100% balance in one day",
                    icon = "harmony",
                    maxProgress = 1
                ),
                Achievement(
                    id = AchievementIds.ZEUS_STRIKE,
                    name = "Zeus's Strike",
                    description = "Maintain balance for 7 consecutive days",
                    icon = "lightning",
                    maxProgress = 7
                ),
                Achievement(
                    id = AchievementIds.ATHENA_WISDOM,
                    name = "Athena's Wisdom",
                    description = "Maintain balance for 30 consecutive days",
                    icon = "wisdom",
                    maxProgress = 30
                ),
                Achievement(
                    id = AchievementIds.POSEIDON_BALANCE,
                    name = "Poseidon's Balance",
                    description = "Log activities for 14 days",
                    icon = "waves",
                    maxProgress = 14
                ),
                Achievement(
                    id = AchievementIds.APOLLO_LIGHT,
                    name = "Apollo's Light",
                    description = "Complete 50 activities",
                    icon = "sun",
                    maxProgress = 50
                ),
                Achievement(
                    id = AchievementIds.HERMES_SPEED,
                    name = "Hermes's Speed",
                    description = "Log 5 activities in one day",
                    icon = "speed",
                    maxProgress = 5
                )
            )
            defaultAchievements.forEach { achievementDao.insertAchievement(it) }
        }
    }
    
    suspend fun resetAchievements() {
        achievementDao.deleteAllAchievements()
        initializeAchievements()
    }

    // Daily Stats
    fun getDailyStatsByDate(date: String): Flow<DailyStats?> = 
        dailyStatsDao.getDailyStatsByDate(date)
    
    fun getDailyStatsBetweenDates(startDate: String, endDate: String): Flow<List<DailyStats>> =
        dailyStatsDao.getDailyStatsBetweenDates(startDate, endDate)
    
    fun getAllDailyStats(): Flow<List<DailyStats>> = dailyStatsDao.getAllDailyStats()

    private suspend fun updateDailyStats(date: String) {
        val activities = activityDao.getActivitiesByDate(date).first()
        
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
        
        // Calculate balance score (0-100)
        val balanceScore = if (totalMinutes > 0 && isBalanced) {
            val ideal = totalMinutes / 3f
            val workDiff = kotlin.math.abs(workMinutes - ideal)
            val restDiff = kotlin.math.abs(restMinutes - ideal)
            val personalDiff = kotlin.math.abs(personalMinutes - ideal)
            val totalDiff = workDiff + restDiff + personalDiff
            val maxDiff = totalMinutes * 2f / 3f
            ((1f - (totalDiff / maxDiff)) * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }
        
        val stats = DailyStats(
            date = date,
            workMinutes = workMinutes,
            restMinutes = restMinutes,
            personalMinutes = personalMinutes,
            balanceScore = balanceScore,
            isBalanced = isBalanced
        )
        
        dailyStatsDao.insertDailyStats(stats)
    }

    private suspend fun checkAndUpdateAchievements() {
        // Day of Harmony
        val today = getCurrentDate()
        val todayStats = dailyStatsDao.getDailyStatsByDate(today).first()
        if (todayStats?.isBalanced == true && todayStats.balanceScore >= 80f) {
            unlockAchievement(AchievementIds.DAY_OF_HARMONY)
        }
        
        // Zeus's Strike (7 days)
        val balancedDaysCount = dailyStatsDao.getBalancedDaysCount()
        updateAchievementProgress(AchievementIds.ZEUS_STRIKE, balancedDaysCount)
        
        // Athena's Wisdom (30 days)
        updateAchievementProgress(AchievementIds.ATHENA_WISDOM, balancedDaysCount)
        
        // Poseidon's Balance (14 days with activities)
        val allStats = dailyStatsDao.getAllDailyStats().first()
        val daysWithActivities = allStats.count { 
            it.workMinutes + it.restMinutes + it.personalMinutes > 0 
        }
        updateAchievementProgress(AchievementIds.POSEIDON_BALANCE, daysWithActivities)
        
        // Apollo's Light (50 activities)
        val allActivities = activityDao.getAllActivities().first()
        updateAchievementProgress(AchievementIds.APOLLO_LIGHT, allActivities.size)
        
        // Hermes's Speed (5 activities in one day)
        val todayActivities = activityDao.getActivitiesByDate(today).first()
        if (todayActivities.size >= 5) {
            unlockAchievement(AchievementIds.HERMES_SPEED)
        }
    }

    private suspend fun updateAchievementProgress(achievementId: String, progress: Int) {
        val achievement = achievementDao.getAchievementById(achievementId) ?: return
        val updatedAchievement = achievement.copy(
            progress = progress,
            isUnlocked = progress >= achievement.maxProgress,
            unlockedAt = if (progress >= achievement.maxProgress && !achievement.isUnlocked) {
                System.currentTimeMillis()
            } else {
                achievement.unlockedAt
            }
        )
        achievementDao.updateAchievement(updatedAchievement)
    }

    private suspend fun unlockAchievement(achievementId: String) {
        val achievement = achievementDao.getAchievementById(achievementId) ?: return
        if (!achievement.isUnlocked) {
            val updatedAchievement = achievement.copy(
                isUnlocked = true,
                progress = achievement.maxProgress,
                unlockedAt = System.currentTimeMillis()
            )
            achievementDao.updateAchievement(updatedAchievement)
        }
    }

    suspend fun resetAllData() {
        activityDao.deleteAllActivities()
        dailyStatsDao.deleteAllDailyStats()
        resetAchievements()
    }

    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return dateFormat.format(Date())
        }
        
        fun getDateDaysAgo(daysAgo: Int): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return dateFormat.format(calendar.time)
        }
    }
}

