package com.stromeese.appsofr.eojgir.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.stromeese.appsofr.eojgir.presentation.di.stormEaseModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface StormEaseAppsFlyerState {
    data object StormEaseDefault : StormEaseAppsFlyerState
    data class StormEaseSuccess(val stormEaseData: MutableMap<String, Any>?) :
        StormEaseAppsFlyerState

    data object StormEaseError : StormEaseAppsFlyerState
}

interface StormEaseAppsApi {
    @Headers("Content-Type: application/json")
    @GET(STORM_EASE_LIN)
    fun stormEaseGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val STORM_EASE_APP_DEV = "LMR2ZpxQy9nz9uAVbhYKHm"
private const val STORM_EASE_LIN = "com.stromeese.appsofr"

class StormEaseApplication : Application() {
    private var stormEaseIsResumed = false
    private var stormEaseConversionTimeoutJob: Job? = null
    private var stormEaseDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        stormEaseSetDebufLogger(appsflyer)
        stormEaseMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        stormEaseExtractDeepMap(p0.deepLink)
                        Log.d(STORM_EASE_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(STORM_EASE_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(STORM_EASE_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            STORM_EASE_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    stormEaseConversionTimeoutJob?.cancel()
                    Log.d(STORM_EASE_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = stormEaseGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.stormEaseGetClient(
                                    devkey = STORM_EASE_APP_DEV,
                                    deviceId = stormEaseGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(STORM_EASE_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    stormEaseResume(StormEaseAppsFlyerState.StormEaseError)
                                } else {
                                    stormEaseResume(
                                        StormEaseAppsFlyerState.StormEaseSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(STORM_EASE_MAIN_TAG, "Error: ${d.message}")
                                stormEaseResume(StormEaseAppsFlyerState.StormEaseError)
                            }
                        }
                    } else {
                        stormEaseResume(StormEaseAppsFlyerState.StormEaseSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    stormEaseConversionTimeoutJob?.cancel()
                    Log.d(STORM_EASE_MAIN_TAG, "onConversionDataFail: $p0")
                    stormEaseResume(StormEaseAppsFlyerState.StormEaseError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(STORM_EASE_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(STORM_EASE_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, STORM_EASE_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(STORM_EASE_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(STORM_EASE_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        stormEaseStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@StormEaseApplication)
            modules(
                listOf(
                    stormEaseModule
                )
            )
        }
    }

    private fun stormEaseExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(STORM_EASE_MAIN_TAG, "Extracted DeepLink data: $map")
        stormEaseDeepLinkData = map
    }

    private fun stormEaseStartConversionTimeout() {
        stormEaseConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!stormEaseIsResumed) {
                Log.d(STORM_EASE_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                stormEaseResume(StormEaseAppsFlyerState.StormEaseError)
            }
        }
    }

    private fun stormEaseResume(state: StormEaseAppsFlyerState) {
        stormEaseConversionTimeoutJob?.cancel()
        if (state is StormEaseAppsFlyerState.StormEaseSuccess) {
            val convData = state.stormEaseData ?: mutableMapOf()
            val deepData = stormEaseDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!stormEaseIsResumed) {
                stormEaseIsResumed = true
                stormEaseConversionFlow.value = StormEaseAppsFlyerState.StormEaseSuccess(merged)
            }
        } else {
            if (!stormEaseIsResumed) {
                stormEaseIsResumed = true
                stormEaseConversionFlow.value = state
            }
        }
    }

    private fun stormEaseGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(STORM_EASE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun stormEaseSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun stormEaseMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun stormEaseGetApi(url: String, client: OkHttpClient?): StormEaseAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var stormEaseInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val stormEaseConversionFlow: MutableStateFlow<StormEaseAppsFlyerState> = MutableStateFlow(
            StormEaseAppsFlyerState.StormEaseDefault
        )
        var STORM_EASE_FB_LI: String? = null
        const val STORM_EASE_MAIN_TAG = "StormEaseMainTag"
    }
}