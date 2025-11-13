package com.stromeese.appsofr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ActivityCategory,
    val durationMinutes: Int,
    val emotion: Emotion,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // Format: YYYY-MM-DD
)

