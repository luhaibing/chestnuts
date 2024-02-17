package com.mercer.annotate.http

import com.mercer.core.Entry
import com.mercer.core.Type

/**
 * 向接口方法快捷追加参数
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Append(
    val value: Type,
    val entry: Entry,
)