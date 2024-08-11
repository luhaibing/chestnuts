package com.mercer.test.lib

import com.google.gson.Gson
import com.mercer.core.Creator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/08/10
 * desc:
 *   api实例创建器
 */
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

    private val gson by lazy { Gson() }

    override fun <T : Any> create(service: KClass<T>): T {
        return retrofit.create(service.java) as T
    }

    override fun <T> suspend2flow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    override fun <T> suspend2deferred(block: suspend () -> T): Deferred<T> {
        val deferred = CompletableDeferred<T>()
        runBlocking {
            deferred.complete(block())
        }
        return deferred
    }

    /*
    override val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }
    */

}