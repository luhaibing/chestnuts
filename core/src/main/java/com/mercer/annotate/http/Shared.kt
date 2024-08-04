package com.mercer.annotate.http

import com.mercer.core.CachePipeline
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/4
 * desc:
 *   热流,共享数据[只能应用于接口]
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Shared(
    val value: KClass<out CachePipeline<*>>
)