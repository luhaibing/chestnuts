package com.mercer.library.test

import com.mercer.core.Creator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

class SimpleCreator : Creator {

    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://example.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(MyInterceptor())
                    .build()
            )
            .build()
    }

    override fun <T : Any> create(service: KClass<T>): T {
        return retrofit.create(service.java) as T
    }

}