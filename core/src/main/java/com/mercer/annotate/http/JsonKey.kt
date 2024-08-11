package com.mercer.annotate.http

/**
 * author:  Mercer
 * date:    2023/12/24
 * desc:
 *   包装成 @Body 时 使用的键和值
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class JsonKey(
    val value: String
)