package com.mercer.annotate.http

import com.mercer.core.Creator
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2023/12/24
 * desc:
 *   注解处理器的触发点
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Decorator(
    val value: KClass<out Creator>
)
