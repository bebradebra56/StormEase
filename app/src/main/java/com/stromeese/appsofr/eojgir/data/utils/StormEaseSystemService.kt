package com.stromeese.appsofr.eojgir.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class StormEaseSystemService(private val context: Context) {

    suspend fun stormEaseGetGaid() : String  = withContext(Dispatchers.IO){
        val gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: "00000000-0000-0000-0000-000000000000"
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Gaid: $gaid")
        return@withContext gaid
    }

    fun stormEaseGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun stormEaseGetLocale() : String {
        return  Locale.getDefault().language
    }

    fun stormEaseIsOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}