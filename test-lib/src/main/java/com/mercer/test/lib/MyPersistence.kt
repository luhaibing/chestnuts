package com.mercer.test.lib

import com.mercer.core.CacheKeys
import com.mercer.core.Deserializer
import com.mercer.core.Path
import com.mercer.core.OnPersistence
import com.mercer.core.Serializer

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   持久化
 */
class MyPersistence : OnPersistence {

    override suspend fun <T : Any?> source(path: Path, cacheKeys: CacheKeys, deserializer: Deserializer<T>): T? {
        val json =
            "{\"code\":\"200\",\"fxLink\":\"https://www.qweather.com/en/weather/chongqing-101040100.html\",\"now\":{\"cloud\":\"999999999999999\",\"dew\":\"8\",\"feelsLike\":\"8\",\"humidity\":\"84\",\"icon\":\"104\",\"obsTime\":\"2024-11-26T15:00+08:00\",\"pressure\":\"994\",\"temp\":\"10\",\"text\":\"阴\",\"vis\":\"12\",\"wind360\":\"0\",\"windDir\":\"北风\",\"windScale\":\"3\",\"windSpeed\":\"12\"},\"refer\":{\"license\":[\"CC BY-SA 4.0\"],\"sources\":[\"QWeather\"]},\"updateTime\":\"2024-11-26T15:03+08:00\"}"
        val value = deserializer(json)
        println("cacheKeys : $cacheKeys")
        println("path : $path")
        println("读取到缓存")
        return value
    }

    override suspend fun <T : Any?> sink(value: T?, path: Path, cacheKeys: CacheKeys, serializer: Serializer<T>) {

    }

}

object MyPersistence2 : OnPersistence {

    override suspend fun <T : Any?> source(path: Path, cacheKeys: CacheKeys, deserializer: Deserializer<T>): T? {
        val json =
            "{\"code\":\"200\",\"fxLink\":\"https://www.qweather.com/en/weather/chongqing-101040100.html\",\"now\":{\"cloud\":\"88888\",\"dew\":\"8\",\"feelsLike\":\"8\",\"humidity\":\"84\",\"icon\":\"104\",\"obsTime\":\"2024-11-26T15:00+08:00\",\"pressure\":\"994\",\"temp\":\"10\",\"text\":\"阴\",\"vis\":\"12\",\"wind360\":\"0\",\"windDir\":\"北风\",\"windScale\":\"3\",\"windSpeed\":\"12\"},\"refer\":{\"license\":[\"CC BY-SA 4.0\"],\"sources\":[\"QWeather\"]},\"updateTime\":\"2024-11-26T15:03+08:00\"}"
        val value = deserializer(json)
        println("读取到缓存")
        return value
    }

    override suspend fun <T : Any?> sink(value: T?, path: Path, cacheKeys: CacheKeys, serializer: Serializer<T>) {

    }

}