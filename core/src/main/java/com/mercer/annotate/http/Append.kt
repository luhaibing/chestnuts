package com.mercer.annotate.http

import com.mercer.core.Provider
import com.mercer.core.Type
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2023/12/24
 * desc:
 *   向接口方法快捷追加参数
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Append(
    val type: Type,
    val key: String,
    val value: KClass<out Provider<*>>,
)