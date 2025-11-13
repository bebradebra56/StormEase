package com.stromeese.appsofr.eojgir.data.shar

import android.content.Context
import androidx.core.content.edit

class StormEaseSharedPreference(context: Context) {
    private val stormEasePrefs = context.getSharedPreferences("stormEaseSharedPrefsAb", Context.MODE_PRIVATE)

    var stormEaseSavedUrl: String
        get() = stormEasePrefs.getString(STORM_EASE_SAVED_URL, "") ?: ""
        set(value) = stormEasePrefs.edit { putString(STORM_EASE_SAVED_URL, value) }

    var stormEaseExpired : Long
        get() = stormEasePrefs.getLong(STORM_EASE_EXPIRED, 0L)
        set(value) = stormEasePrefs.edit { putLong(STORM_EASE_EXPIRED, value) }

    var stormEaseAppState: Int
        get() = stormEasePrefs.getInt(STORM_EASE_APPLICATION_STATE, 0)
        set(value) = stormEasePrefs.edit { putInt(STORM_EASE_APPLICATION_STATE, value) }

    var stormEaseNotificationRequest: Long
        get() = stormEasePrefs.getLong(STORM_EASE_NOTIFICAITON_REQUEST, 0L)
        set(value) = stormEasePrefs.edit { putLong(STORM_EASE_NOTIFICAITON_REQUEST, value) }

    var stormEaseNotificationRequestedBefore: Boolean
        get() = stormEasePrefs.getBoolean(STORM_EASE_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = stormEasePrefs.edit { putBoolean(
            STORM_EASE_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val STORM_EASE_SAVED_URL = "stormEaseSavedUrl"
        private const val STORM_EASE_EXPIRED = "stormEaseExpired"
        private const val STORM_EASE_APPLICATION_STATE = "stormEaseApplicationState"
        private const val STORM_EASE_NOTIFICAITON_REQUEST = "stormEaseNotificationRequest"
        private const val STORM_EASE_NOTIFICATION_REQUEST_BEFORE = "stormEaseNotificationRequestedBefore"
    }
}