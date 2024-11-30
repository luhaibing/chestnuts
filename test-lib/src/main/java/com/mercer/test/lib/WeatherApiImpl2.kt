package com.mercer.test.lib

import com.mercer.core.CacheKeys
import com.mercer.core.Converter
import com.mercer.core.Creator
import com.mercer.core.DefaultPersistenceDispatcher
import com.mercer.core.Path
import com.mercer.core.SelectPersistenceDispatcher
import com.mercer.test.lib.weather.Now
import com.mercer.test.lib.weather.Response
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.typeOf

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   天气
 */
class WeatherApiImpl2 private constructor() : WeatherApi {

    private val onCreator: Creator by lazy {
        SimpleCreator()
    }

    private val api: WeatherApiService by lazy {
        onCreator.create(WeatherApiService::class)
    }

    public companion object {
        public operator fun invoke(): WeatherApiImpl2 = Holder.INSTANCE
    }

    private object Holder {
        public val INSTANCE: WeatherApiImpl2 = WeatherApiImpl2()
    }


    override fun now(location: String, key: String, now: String): Flow<Response<Now>> {
        val v1 = CacheKeys()
        v1["location"] = location
        val v2 = Path.GET("v7/weather/$now")
        val v3 = MoshiConverterFactory<Response<Now>>(typeOf<Response<Now>>())
        val v4 = MyPersistence()
        val execute: suspend () -> Response<Now> = {
            api.now_BFF177EACEE0BEFF1CB458AD8E5CB062(location, key, now)
        }
        return DefaultPersistenceDispatcher(v2, v1, v3, execute = execute, source = v4::source, sink = v4::sink)
    }

    override suspend fun now2(location: String, key: String, temp: String): Response<Now> {
        TODO("Not yet implemented")
    }

    override suspend fun now3(location: String, key: String, temp: String): Response<Now> {
        TODO("Not yet implemented")
    }

    private val converters: ConcurrentHashMap<String, Converter.Factory<*>> by lazy { ConcurrentHashMap<String, Converter.Factory<*>>() }
    override fun now4(location: String, key: String): Flow<Response<Now>> {
        val v1 = CacheKeys()
        v1["location"] = api
        v1["id"] = onCreator
        val v2 = Path.GET("v7/weather/now")
        val v3 = converters.getOrPut("now4_7C20B83C52E4F58CB875BB8BF63AE242"){
            MoshiConverterFactory<Response<Now>>(typeOf<Response<Now>>())
        } as Converter.Factory<Response<Now>>
        val v4 = MyPersistence2
        val v5: suspend () -> Response<Now> = {
            this.api.now4_7C20B83C52E4F58CB875BB8BF63AE242(v1 = location, v2 = key)
        }
        return SelectPersistenceDispatcher(v2, v1, v3, execute = v5, source = v4::source, sink = v4::sink)
    }

}
