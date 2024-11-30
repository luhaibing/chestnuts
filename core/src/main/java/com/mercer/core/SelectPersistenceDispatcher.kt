package com.mercer.core

import com.mercer.core.entity.Data
import com.mercer.core.entity.From
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
 * @Created on 2024/11/27.
 * @Description:
 *   选择模式的调度器(并行获取直至获取到网络数据)
 */
object SelectPersistenceDispatcher : PersistenceDispatcher {

    override fun <T> invoke(
        path: Path,
        cacheKeys: CacheKeys,
        converter: Converter.Factory<T>,
        execute: Execute<T>,
        source: Source<T>,
        sink: Sink<T>,
    ): Flow<T> {
        return flow {
            supervisorScope {
                val fromCacheDeferred: Deferred<T> = async {
                    val value = source(path, cacheKeys, converter.deserializer)
                    while (value == null) delay(1.seconds)
                    value
                }
                val fromNetworkDeferred: Deferred<T> = async { execute() }
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
}