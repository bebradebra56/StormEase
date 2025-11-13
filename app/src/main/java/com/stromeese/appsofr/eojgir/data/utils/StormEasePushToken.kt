package com.stromeese.appsofr.eojgir.data.utils

import android.util.Log
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StormEasePushToken {

    suspend fun stormEaseGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}