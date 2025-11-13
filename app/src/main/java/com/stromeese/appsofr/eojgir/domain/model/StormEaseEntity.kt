package com.stromeese.appsofr.eojgir.domain.model

import com.google.gson.annotations.SerializedName


data class StormEaseEntity (
    @SerializedName("ok")
    val stormEaseOk: String,
    @SerializedName("url")
    val stormEaseUrl: String,
    @SerializedName("expires")
    val stormEaseExpires: Long,
)