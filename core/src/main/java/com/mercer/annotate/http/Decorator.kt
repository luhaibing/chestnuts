package com.mercer.annotate.http

import com.mercer.core.Creator
import kotlin.reflect.KClass

/**
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Decorator(
    val value: KClass<out Creator>,
)