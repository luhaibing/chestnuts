@file:Suppress("unused")

package com.mercer.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.seconds

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

/**
 * 默认的缓存使用策略
 */
fun <T> defaultCacheUseStrategy(
    onNetwork: suspend () -> T, onRead: suspend () -> T?, onWrite: suspend (T?) -> Unit
): Flow<T> {
    /*
    // 使用 flow 操作符实现
    return flow<Result<T>> {
        val v = onRead()
        if (v != null) {
            emit(Result(v, Source.Cache))
        }
    }.onCompletion {
        emit(Result(onNetwork(), Source.Network))
    }.onEach {
        if (it.source == Source.Network && it.value != null) {
            onWrite(it.value)
        }
    }.map {
        it.value
    }
    */
    return flow {
        val cache = onRead()
        if (cache != null) {
            emit(Result(cache, Source.Cache))
        }
        val resp = onNetwork()
        emit(Result(resp, Source.Network))
    }.onEach {
        if (it.source == Source.Network /* && it.value != null */) {
            onWrite(it.value)
        }
    }.map {
        it.value
    }
}

fun <T> selectCacheUseStrategy(
    onNetwork: suspend () -> T, onRead: suspend () -> T?, onWrite: suspend (T?) -> Unit
): Flow<T> {
    /*
    // 使用 flow 操作符实现
    return merge(flow {
        val cache = onRead()
        if (cache != null) {
            emit(Result(cache, Source.Cache))
        }
    }, flow {
        val resp = onNetwork()
        emit(Result(resp, Source.Network))
    }).takeWhile {
        it.source == Source.Network
    }.onEach {
        if (it.source == Source.Network && it.value != null) {
            onWrite( it.value)
        }
    }.map {
        it.value
    }
    */
    return flow {
        supervisorScope {
            val fromCacheDeferred: Deferred<T> = async {
                val value = onRead()
                while (value == null) delay(1.seconds)
                value
            }
            val fromNetworkDeferred: Deferred<T> = async { onNetwork() }
            val result = select {
                fromCacheDeferred.onAwait { Result(it, Source.Cache) }
                fromNetworkDeferred.onAwait { Result(it, Source.Network) }
            }
            emit(result)
            if (result.source != Source.Network) {
                emit(Result(fromNetworkDeferred.await(), Source.Network))
            }
            fromCacheDeferred.cancel()
            fromNetworkDeferred.cancel()
        }
    }.onEach {
        if (it.source == Source.Network /* && it.value != null */) {
            onWrite(it.value)
        }
    }.map {
        it.value
    }
}