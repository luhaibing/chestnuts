package com.mercer.core

/**
 * @author      : Mercer
 * @Created     : 2025/01/03.
 * @Description :
 *   类型别名
 */
typealias Execute<T> = suspend () -> T
typealias Source<T> = suspend (Path, CacheKeys, Deserializer<T>) -> T?
typealias Sink<T> = suspend (T?, Path, CacheKeys, Serializer<T>) -> Unit