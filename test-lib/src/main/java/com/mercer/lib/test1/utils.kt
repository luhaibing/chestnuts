package com.mercer.lib.test1

import com.mercer.lib.test2.MyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val testApi: TestApi = Retrofit
    .Builder()
    .baseUrl("https://example.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        OkHttpClient.Builder()
            .addInterceptor(MyInterceptor())
            .build()
    )
    .build()
    .create(TestApi::class.java)


val MAP = mapOf(
    "k1" to "1",
    "k2" to "22",
)