package com.mercer.test.lib.weather

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class Refer(
    val license: List<String>,
    val sources: List<String>
)