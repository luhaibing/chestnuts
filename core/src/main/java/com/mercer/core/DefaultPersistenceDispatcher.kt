package com.mercer.core

import com.mercer.core.entity.Data
import com.mercer.core.entity.From
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * @author :Mercer
 * @Created on 2024/11/27.
 * @Description:
 *   默认的调度器(串行获取)
 */
object DefaultPersistenceDispatcher : PersistenceDispatcher {
    override fun <T> invoke(
        path: Path,
        cacheKeys: CacheKeys,
        converter: Converter.Factory<T>,
        execute: Execute<T>,
        source: Source<T>,
        sink: Sink<T>,
    ): Flow<T> {
        return flow {
            val cache = source(path, cacheKeys, converter.deserializer)
            if (cache != null) {
                emit(Data(cache, From.Cache))
            }
            val resp = execute()
            emit(Data(resp, From.Network))
        }.onEach {
            if (it.from == From.Network /* && it.value != null */) {
                sink(it.value, path, cacheKeys, converter.serializer)
            }
        }.map {
            it.value
        }
    }
}