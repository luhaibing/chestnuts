package com.mercer.annotate.http

import com.mercer.core.Pipeline
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/8/9
 * desc:
 *   热流,共享数据(只能应用于接口)
 *   存取数据时,使用的 path 为 第一个抽象方法的 path
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class State(
    val value: KClass<out Pipeline<*>>
)