package com.mercer.core

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
 * author:  Mercer
 * date:    2024/8/3
 * desc:
 *   缓存相关
 */

/**
 * 数据来源
 */
sealed interface Source {
    // 缓存
    data object Cache : Source

    // 网络
    data object Network : Source
}

/**
 * 数据
 */
internal data class Result<out T>(
    val value: T,
    val source: Source
)

/**
 * 缓存的模式
 */
enum class Mode : CacheStrategy {
    /**
     * 先尝试读取缓存,如果缓存不为空,就先发送一次],再进行网络请求
     */
    DEFAULT {
        override operator fun <T> invoke(
            path: Path,
            pipeline: CachePipeline<T>,
            onNetwork: suspend () -> T,
        ): Flow<T> {
            /*
            // 使用 flow 操作符实现
            return flow<Result<T>> {
                val v = pipeline.read(path)
                if (v != null) {
                    emit(Result(v, Source.Cache))
                }
            }.catch {
                it.printStackTrace()
            }.onCompletion {
                emit(Result(onNetwork(), Source.Network))
            }.onEach {
                if (it.source == Source.Network && it.value != null) {
                    pipeline.write(path, it.value)
                }
            }.map {
                it.value
            }
            */
            return flow {
                val cache = pipeline.read(path)
                if (cache != null) {
                    emit(Result(cache, Source.Cache))
                }
                val resp = onNetwork()
                emit(Result(resp, Source.Network))
            }.onEach {
                if (it.source == Source.Network && it.value != null) {
                    pipeline.write(path, it.value)
                }
            }.map {
                it.value
            }
        }
    },

    /**
     * 读取缓存和网络请求同时开始,哪个先返回,先使用哪个,如果先获取到是网络响应就直接发送,如果是缓存那就再进行网络请求,
     */
    SELECT {
        override fun <T> invoke(
            path: Path,
            pipeline: CachePipeline<T>,
            onNetwork: suspend () -> T,
        ): Flow<T> {
            /*
            // 使用 flow 操作符实现
            return merge(flow {
                val cache = pipeline.read(path)
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
                    pipeline.write(path, it.value)
                }
            }.map {
                it.value
            }
            */
            return flow {
                supervisorScope {
                    val fromCacheDeferred: Deferred<T> = async {
                        val value = pipeline.read(path)
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
                        emit(Result(onNetwork(), Source.Network))
                    }
                    fromCacheDeferred.cancel()
                    fromNetworkDeferred.cancel()
                }
            }.onEach {
                if (it.source == Source.Network && it.value != null) {
                    pipeline.write(path, it.value)
                }
            }.map {
                it.value
            }
        }
    };
}

/**
 * 进行缓存的管道
 */
interface CachePipeline<T> {

    suspend fun read(path: Path?): T?

    suspend fun write(path: Path, value: T?)

}

/**
 * 原本策略的实现想通过 flow 现有操作符来实现
 * 但是 SELECT 模式的实现还不够完善(一旦接收到网络数据,同时结束)
 */
internal interface CacheStrategy {

    operator fun <T> invoke(
        path: Path,
        pipeline: CachePipeline<T>,
        onNetwork: suspend () -> T,
    ): Flow<T>

}