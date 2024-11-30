package com.mercer.test.lib.weather

import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class Response<T : Any?>(
    val code: String,
    val fxLink: String,
    val now: T,
    val refer: Refer,
    val updateTime: String
)