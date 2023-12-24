package com.mercer.lib.test1

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun main() = runBlocking {
    testApi.func3(
        v1 = "sem",
        v2 = "persecuti",
        v3 = "2222".toRequestBody("text/plain".toMediaType()),
        v4 = MultipartBody.Part.create(
            "333".toRequestBody("text/plain".toMediaType())
        ),
        v5 = MultipartBody.Part.createFormData(
            name = "Rusty Sanford", value = "mandamus"
        ),
        v6 = MAP
    )
    Unit
}