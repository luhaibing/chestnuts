package com.mercer.annotate.http

import com.mercer.core.Mode
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/7/28
 * desc:
 *   缓存
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Cache(
    val value: KClass<*>,
    val mode: Mode = Mode.DEFAULT
)