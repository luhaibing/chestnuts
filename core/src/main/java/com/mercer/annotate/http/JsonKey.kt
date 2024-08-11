package com.mercer.annotate.http

/**
 * author:  Mercer
 * date:    2024/2/14 08:27
 * desc:
 *   包装成 @Body 时 使用的键和值
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class JsonKey(
    val value: String
)