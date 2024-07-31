package com.mercer.annotate.http

import com.mercer.core.Creator
import com.mercer.core.Converter
import kotlin.reflect.KClass

/**
 * 注解处理器的触发点
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Decorator(
    val value: KClass<out Creator>,
    val converter: KClass<out Converter>
)