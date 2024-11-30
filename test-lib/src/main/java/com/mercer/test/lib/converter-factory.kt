package com.mercer.test.lib

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mercer.core.Converter
import com.mercer.core.Deserializer
import com.mercer.core.Serializer
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.javaType

/**
 * @author :Mercer
 * @Created on 2024/11/27.
 * @Description:
 *
 */
class MoshiConverterFactory<T>(value: KType,) : Converter.Factory<T>(value) {

    private val moshi  by lazy {
        Moshi.Builder()
            // .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
    }

    @OptIn(ExperimentalStdlibApi::class)
    val adapter by lazy { moshi.adapter<T>(value) }

    override val serializer: Serializer<T> = object : Serializer<T?> {
        override suspend fun invoke(value: T?): String? {
            return null
        }
    }

    override val deserializer: Deserializer<T> = object : Deserializer<T?> {
        override suspend fun invoke(value: String?): T? {
            value ?: return null
            return adapter.fromJson(value)
        }
    }

}

class KsConverterFactory<T>(value: KType,) : Converter.Factory<T>(value) {

    private val client by lazy {
        Json {}
    }

    @OptIn(ExperimentalStdlibApi::class)
    val kSerializer by lazy {
        client.serializersModule.serializer(value) as KSerializer<T>
    }

    override val serializer: Serializer<T> = object : Serializer<T?> {
        override suspend fun invoke(value: T?): String? {
            return null
        }
    }

    override val deserializer: Deserializer<T> = object : Deserializer<T?> {
        override suspend fun invoke(value: String?): T? {
            value?:return null
            return client.decodeFromString(kSerializer, value)
        }
    }

}

class GsonConverterFactory<T>(value: KType) : Converter.Factory<T>(value) {

    val gson by lazy { Gson() }

    @OptIn(ExperimentalStdlibApi::class)
    val adapter by lazy { gson.getAdapter(TypeToken.get(value.javaType) as TypeToken<T>) }

    override val serializer: Serializer<T?> = object : Serializer<T?> {
        override suspend fun invoke(value: T?): String? {
            return null
        }
    }

    override val deserializer: Deserializer<T?> = object : Deserializer<T?> {
        override suspend fun invoke(value: String?): T? {
            return adapter.fromJson(value)
        }
    }

}