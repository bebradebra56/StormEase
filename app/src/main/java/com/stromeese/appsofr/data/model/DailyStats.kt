package com.stromeese.appsofr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val workMinutes: Int = 0,
    val restMinutes: Int = 0,
    val personalMinutes: Int = 0,
    val balanceScore: Float = 0f, // 0-100
    val isBalanced: Boolean = false // True if all three categories have activities
)

