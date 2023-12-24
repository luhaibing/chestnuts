package com.mercer.lib.test2

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val testApi: TestKotlin = Retrofit
    .Builder()
    .baseUrl("https://example.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        OkHttpClient.Builder()
            .addInterceptor(MyInterceptor())
            .build()
    )
    .build()
    .create(TestKotlin::class.java)


class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        print("*************")
        print(" ")
        println("MyInterceptor")
        print(" ")
        print("*************")
        println()
        val request = chain.request()
        println()
        println()
        return chain.proceed(request)
    }

}