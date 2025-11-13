package com.stromeese.appsofr.eojgir.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication

class StormEasePushHandler {
    fun stormEaseHandlePush(extras: Bundle?) {
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = stormEaseBundleToMap(extras)
            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    StormEaseApplication.STORM_EASE_FB_LI = map["url"]
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Push data no!")
        }
    }

    private fun stormEaseBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}