package com.stromeese.appsofr.eojgir.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stromeese.appsofr.R
import com.stromeese.appsofr.StormEaseActivity
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication

private const val STORM_EASE_CHANNEL_ID = "storm_ease_notifications"
private const val STORM_EASE_CHANNEL_NAME = "StormEase Notifications"
private const val STORM_EASE_NOT_TAG = "StormEase"

class StormEasePushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                stormEaseShowNotification(it.title ?: STORM_EASE_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                stormEaseShowNotification(it.title ?: STORM_EASE_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            stormEaseHandleDataPayload(remoteMessage.data)
        }
    }

    private fun stormEaseShowNotification(title: String, message: String, data: String?) {
        val stormEaseNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                STORM_EASE_CHANNEL_ID,
                STORM_EASE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            stormEaseNotificationManager.createNotificationChannel(channel)
        }

        val stormEaseIntent = Intent(this, StormEaseActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val stormEasePendingIntent = PendingIntent.getActivity(
            this,
            0,
            stormEaseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stormEaseNotification = NotificationCompat.Builder(this, STORM_EASE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.storm_ease_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(stormEasePendingIntent)
            .build()

        stormEaseNotificationManager.notify(System.currentTimeMillis().toInt(), stormEaseNotification)
    }

    private fun stormEaseHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}