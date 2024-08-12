package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/8/11
 * desc:
 *   序列化
 */
interface Serializer {
    /**
     * 序列化
     */
    fun <T> serialize(value: T?): String?
}

interface GsonSerializer : Serializer {
    /**
     * 反序列化
     */
    fun <T> deserialize(value: String?, type: java.lang.reflect.Type): T
}

interface MoshiSerializer : Serializer {
    /**
     * 反序列化
     */
    fun <T> deserialize(value: String?, type: java.lang.reflect.Type): T
}

interface KsSerializer : Serializer