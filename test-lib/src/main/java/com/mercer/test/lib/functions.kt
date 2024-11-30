package com.mercer.test.lib

import com.mercer.core.CacheKeys
import com.mercer.core.Converter
import com.mercer.core.Deserializer
import com.mercer.core.Path
import com.mercer.core.Serializer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.seconds

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   方法
 */
/**
 * 数据来源
 */
sealed interface From {
    /*
     * 缓存
     */
    data object Cache : From

    /*
     * 网络
     */
    data object Network : From
}

/**
 * 数据
 */
data class Data<out T : Any?>(val value: T, val from: From)

fun <T> defaultCacheUseStrategy(
    path: Path,
    cacheKeys: CacheKeys,
    converter: Converter.Factory<T>,
    onNetwork: suspend () -> T,
    source: suspend (Path, CacheKeys, Deserializer<T>) -> T?,
    sink: suspend (T?, Path, CacheKeys, Serializer<T>) -> Unit
): Flow<T> {
    return flow {
        val cache = source(path, cacheKeys, converter.deserializer)
        if (cache != null) {
            emit(Data(cache, From.Cache))
        }
        val resp = onNetwork()
        emit(Data(resp, From.Network))
    }.onEach {
        if (it.from == From.Network /* && it.value != null */) {
            sink(it.value, path, cacheKeys, converter.serializer)
        }
    }.map {
        it.value
    }
}

fun <T> selectCacheUseStrategy(
    path: Path,
    cacheKeys: CacheKeys,
    converter: Converter.Factory<T>,
    onNetwork: suspend () -> T,
    source: suspend (Path, CacheKeys, Deserializer<T>) -> T?,
    sink: suspend (T?, Path, CacheKeys, Serializer<T>) -> Unit
): Flow<T> {
    return flow {
        supervisorScope {
            val fromCacheDeferred: Deferred<T> = async {
                val value = source(path, cacheKeys, converter.deserializer)
                while (value == null) delay(1.seconds)
                value
            }
            val fromNetworkDeferred: Deferred<T> = async { onNetwork() }
            val result = select {
                fromCacheDeferred.onAwait { Data(it, From.Cache) }
                fromNetworkDeferred.onAwait { Data(it, From.Network) }
            }
            emit(result)
            if (result.from != From.Network) {
                emit(Data(fromNetworkDeferred.await(), From.Network))
            }
            fromCacheDeferred.cancel()
            fromNetworkDeferred.cancel()
        }
    }.onEach {
        if (it.from == From.Network /* && it.value != null */) {
            sink(it.value, path, cacheKeys, converter.serializer)
        }
    }.map {
        it.value
    }
}