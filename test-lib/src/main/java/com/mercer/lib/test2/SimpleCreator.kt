package com.mercer.lib.test2

import com.google.gson.Gson
import com.mercer.core.Creator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun <T> suspend2flow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    private val gson by lazy { Gson() }

    override fun any2str(value: Any?): String? {
        return value?.let { gson.toJson(it) }
    }

}