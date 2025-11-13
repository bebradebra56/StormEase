package com.stromeese.appsofr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // Icon identifier
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

object AchievementIds {
    const val DAY_OF_HARMONY = "day_of_harmony"
    const val ZEUS_STRIKE = "zeus_strike"
    const val ATHENA_WISDOM = "athena_wisdom"
    const val POSEIDON_BALANCE = "poseidon_balance"
    const val APOLLO_LIGHT = "apollo_light"
    const val HERMES_SPEED = "hermes_speed"
}

