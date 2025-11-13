package com.stromeese.appsofr.data.dao

import androidx.room.*
import com.stromeese.appsofr.data.model.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE date = :date ORDER BY timestamp DESC")
    fun getActivitiesByDate(date: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getActivitiesBetweenDates(startDate: String, endDate: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: Long): Activity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity): Long

    @Delete
    suspend fun deleteActivity(activity: Activity)

    @Query("DELETE FROM activities")
    suspend fun deleteAllActivities()

    @Query("SELECT SUM(durationMinutes) FROM activities WHERE date = :date")
    suspend fun getTotalMinutesForDate(date: String): Int?
}

