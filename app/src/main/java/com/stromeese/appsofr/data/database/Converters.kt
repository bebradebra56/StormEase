package com.stromeese.appsofr.data.database

import androidx.room.TypeConverter
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.Emotion

class Converters {
    @TypeConverter
    fun fromActivityCategory(value: ActivityCategory): String {
        return value.name
    }

    @TypeConverter
    fun toActivityCategory(value: String): ActivityCategory {
        return ActivityCategory.valueOf(value)
    }

    @TypeConverter
    fun fromEmotion(value: Emotion): String {
        return value.name
    }

    @TypeConverter
    fun toEmotion(value: String): Emotion {
        return Emotion.valueOf(value)
    }
}

