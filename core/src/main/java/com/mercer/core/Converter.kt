package com.mercer.core

import java.io.IOException
import kotlin.reflect.KType

/**
 * @author :Mercer
 * @Created on 2024/11/24.
 * @Description:
 *   转化器
 */
interface Converter<In, Out> where In : Any?, Out : Any? {

    @Throws(IOException::class)
    suspend operator fun invoke(value: In): Out

    abstract class Factory<T : Any?> constructor(value: KType) {
        abstract val serializer: Serializer<T>
        abstract val deserializer: Deserializer<T>
    }

}

/**
 * 序列化器
 */
typealias Serializer<T> = Converter<T?, String?>

/**
 * 反序列化器
 */
typealias Deserializer<T> = Converter<String?, T?>