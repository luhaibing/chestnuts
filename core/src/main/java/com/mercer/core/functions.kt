package com.mercer.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

/**
 * author:  Mercer
 * date:    2024/8/4
 * desc:
 *   方法
 */
/**
 * 挂起函数 转 Deferred
 */
fun <T> suspend2deferred(block: suspend () -> T): Deferred<T> {
    val deferred = CompletableDeferred<T>()
    runBlocking {
        deferred.complete(block())
    }
    return deferred
}