package com.stromeese.appsofr.eojgir.domain.model

import com.google.gson.annotations.SerializedName


private const val STORM_EASE_A = "com.stromeese.appsofr"
private const val STORM_EASE_B = "stormease-1cb81"
data class StormEaseParam (
    @SerializedName("af_id")
    val stormEaseAfId: String,
    @SerializedName("bundle_id")
    val stormEaseBundleId: String = STORM_EASE_A,
    @SerializedName("os")
    val stormEaseOs: String = "Android",
    @SerializedName("store_id")
    val stormEaseStoreId: String = STORM_EASE_A,
    @SerializedName("locale")
    val stormEaseLocale: String,
    @SerializedName("push_token")
    val stormEasePushToken: String,
    @SerializedName("firebase_project_id")
    val stormEaseFirebaseProjectId: String = STORM_EASE_B,

    )