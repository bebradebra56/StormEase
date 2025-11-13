package com.stromeese.appsofr.eojgir.data.repo

import android.util.Log
import com.stromeese.appsofr.eojgir.domain.model.StormEaseEntity
import com.stromeese.appsofr.eojgir.domain.model.StormEaseParam
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication.Companion.STORM_EASE_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface StormEaseApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun stormEaseGetClient(
        @Body jsonString: JsonObject,
    ): Call<StormEaseEntity>
}


private const val STORM_EASE_MAIN = "https://sttormease.com/"
class StormEaseRepository {

    suspend fun stormEaseGetClient(
        stormEaseParam: StormEaseParam,
        stormEaseConversion: MutableMap<String, Any>?
    ): StormEaseEntity? {
        val gson = Gson()
        val api = stormEaseGetApi(STORM_EASE_MAIN, null)

        val stormEaseJsonObject = gson.toJsonTree(stormEaseParam).asJsonObject
        stormEaseConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            stormEaseJsonObject.add(key, element)
        }
        return try {
            val stormEaseRequest: Call<StormEaseEntity> = api.stormEaseGetClient(
                jsonString = stormEaseJsonObject,
            )
            val stormEaseResult = stormEaseRequest.awaitResponse()
            Log.d(STORM_EASE_MAIN_TAG, "Retrofit: Result code: ${stormEaseResult.code()}")
            if (stormEaseResult.code() == 200) {
                Log.d(STORM_EASE_MAIN_TAG, "Retrofit: Get request success")
                Log.d(STORM_EASE_MAIN_TAG, "Retrofit: Code = ${stormEaseResult.code()}")
                Log.d(STORM_EASE_MAIN_TAG, "Retrofit: ${stormEaseResult.body()}")
                stormEaseResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(STORM_EASE_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(STORM_EASE_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun stormEaseGetApi(url: String, client: OkHttpClient?) : StormEaseApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
