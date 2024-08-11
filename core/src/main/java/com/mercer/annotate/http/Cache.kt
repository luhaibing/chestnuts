package com.mercer.annotate.http

import com.mercer.core.Pipeline
import com.mercer.core.Strategy
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/3
 * desc:
 *   缓存响应(仅限Flow)
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Cache(
    val pipeline: KClass<out Pipeline<*>>,
    val strategy: Strategy = Strategy.DEFAULT
)