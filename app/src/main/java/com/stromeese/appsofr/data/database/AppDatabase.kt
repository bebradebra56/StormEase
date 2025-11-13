package com.stromeese.appsofr.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stromeese.appsofr.data.dao.ActivityDao
import com.stromeese.appsofr.data.dao.AchievementDao
import com.stromeese.appsofr.data.dao.DailyStatsDao
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.Achievement
import com.stromeese.appsofr.data.model.DailyStats

@Database(
    entities = [
        Activity::class,
        Achievement::class,
        DailyStats::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyStatsDao(): DailyStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stormease_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

