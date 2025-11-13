package com.stromeese.appsofr.data.dao

import androidx.room.*
import com.stromeese.appsofr.data.model.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllDailyStats(): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getDailyStatsByDate(date: String): Flow<DailyStats?>

    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDailyStatsBetweenDates(startDate: String, endDate: String): Flow<List<DailyStats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStats(stats: DailyStats)

    @Query("DELETE FROM daily_stats")
    suspend fun deleteAllDailyStats()

    @Query("SELECT COUNT(*) FROM daily_stats WHERE isBalanced = 1")
    suspend fun getBalancedDaysCount(): Int
}

