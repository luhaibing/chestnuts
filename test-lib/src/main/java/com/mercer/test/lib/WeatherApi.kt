package com.mercer.test.lib

import com.mercer.annotate.http.CacheKey
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.Persistence
import com.mercer.annotate.http.Serialization
import com.mercer.core.SelectPersistenceDispatcher
import com.mercer.test.lib.weather.Now
import com.mercer.test.lib.weather.Response
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   天气Api
 */
@Temp
@Decorator(SimpleCreator::class)
@Serialization(MoshiConverterFactory::class)
interface WeatherApi {

    @Persistence(value = MyPersistence::class)
    @GET("v7/weather/{now}")
    fun now(
        @Path("now") now: String,
        @CacheKey("location") @Query("location") location: String,
        @Temp @CacheKey("id") @Query("key") key: String,
    ): Flow<Response<Now>>

    @Persistence(value = MyPersistence::class)
    @Temp
    @GET("v7/weather/now")
    suspend fun now2(
        @Query("location") location: String,
        @Query("key") key: String,
        @Temp temp: String,
    ): Response<Now>

    @Persistence(value = MyPersistence::class)
    @GET("v7/weather/now")
    fun now3(
        @Query("location") location: String,
        @Query("key") key: String,
        @Temp temp: String,
    ): Deferred<Response<Now>>

    @Persistence(value = MyPersistence2::class, dispatcher = SelectPersistenceDispatcher::class)
    @GET("v7/weather/now")
    fun now4(
        @CacheKey("location") @Query("location") api: String,
        @CacheKey("id") @Query("key") converters: String,
    ): Flow<Response<Now>>

    companion object {
        operator fun invoke(): WeatherApi {
            return WeatherApiImpl()
        }
    }

}

fun main() = runBlocking {
    val weatherApi = WeatherApi()
    weatherApi.now4("101040100", "0e79ac89a9414dd28106035b628dc52b")
        .onEach {
            // println(Gson().toJson(it))
            println("v1 >>> $it")
        }
        .launchIn(this)
    val result1 = weatherApi.now2("101040100", "0e79ac89a9414dd28106035b628dc52b", "xxx")
    println("v2 >>> $result1")
    val result2 = weatherApi.now3("101040100", "0e79ac89a9414dd28106035b628dc52b","232").await()
    println("v3 >>> $result2")
    Unit
}

// https://devapi.qweather.com/v7/weather/now?location=101040100&key=0e79ac89a9414dd28106035b628dc52b