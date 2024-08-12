package com.mercer.annotate.http

import com.mercer.core.Serializer
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/11
 * desc:
 *   序列化
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Serialization(
    val value: KClass<out Serializer>
)
