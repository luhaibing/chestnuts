package com.mercer.core

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.seconds
import com.mercer.core.Source.*

/**
 * @author :Mercer
 * @Created on 2024/7/30.
 * @Description:
 * 缓存相关
 */
/**
 * 缓存的模式
 */
enum class Mode {
    // 先尝试读取缓存,如果缓存不为空,就先发送一次],再进行网络请求
    DEFAULT,

    // 读取缓存和网络请求同时开始,哪个先返回,先使用哪个,如果先获取到是网络响应就直接发送,如果是缓存那就再进行网络请求,
    SELECT
}

/**
 * 缓存策略
 */
sealed interface CacheStrategy {
    /**
     * 处理
     */
    suspend fun <T> process(
        collector: FlowCollector<Result<T>>,
        path: Path,
        onCache: suspend (Path) -> T?,
        onNetwork: suspend () -> T
    )
}

object DefaultCacheStrategy : CacheStrategy {

    override suspend fun <T> process(
        collector: FlowCollector<Result<T>>,
        path: Path,
        onCache: suspend (Path) -> T?,
        onNetwork: suspend () -> T
    ) {
        val cache = onCache(path)
        if (cache != null) {
            collector.emit(Result(cache, CACHE))
        }
        val resp = onNetwork()
        collector.emit(Result(resp, NETWORK))
    }
}

object SelectedCacheStrategy : CacheStrategy {

    override suspend fun <T> process(
        collector: FlowCollector<Result<T>>,
        path: Path,
        onCache: suspend (Path) -> T?,
        onNetwork: suspend () -> T
    ) {
        /*
        val value = supervisorScope {
            val fromCacheDeferred: Deferred<T> = async {
                val value = fromCache(path)
                while (value == null) {
                    delay(1.seconds)
                }
                value
            }
            val fromNetworkDeferred: Deferred<T> = async {
                fromNetwork()
            }
            val result = select {
                fromCacheDeferred.onAwait {
                    Result(it, CACHE)
                }
                fromNetworkDeferred.onAwait {
                    Result(it, NETWORK)
                }
            }
            fromCacheDeferred.cancel()
            fromNetworkDeferred.cancel()
            result
        }
        collector.emit(value)
        if (value.source != NETWORK) {
            collector.emit(Result(fromNetwork(), NETWORK))
        }
        */
        supervisorScope {
            val fromCacheDeferred = async {
                val value = onCache(path)
                while (value == null) {
                    delay(1.seconds)
                }
                value
            }
            val fromNetworkDeferred = async {
                onNetwork()
            }
            val result = select {
                fromCacheDeferred.onAwait {
                    Result(it, CACHE)
                }
                fromNetworkDeferred.onAwait {
                    Result(it, NETWORK)
                }
            }
            collector.emit(result)
            if (result.source != NETWORK) {
                val resp = fromNetworkDeferred.await()
                collector.emit(Result(resp, NETWORK))
            }
            fromCacheDeferred.cancel()
            fromNetworkDeferred.cancel()
        }
    }

}