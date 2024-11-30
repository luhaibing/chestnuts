package com.mercer.annotate.http

/**
 * @author :Mercer
 * @Created on 2024/11/24.
 * @Description:
 *   缓存使用的键
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CacheKey(val value: String)
