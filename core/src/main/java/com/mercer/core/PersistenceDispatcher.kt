package com.mercer.core

import kotlinx.coroutines.flow.Flow

/**
 * @author :Mercer
 * @Created on 2024/11/27.
 * @Description:
 *   持久化数据的调度器
 *   在同一个接口中,不同的方法,如果使用的同一个类型，那么就是同一个调度器
 *   如果需要全局为同一个调度器，那么可以直接声明为 object 类
 */
interface PersistenceDispatcher {

    /**
     * 执行请求
     */
    operator fun <T : Any?> invoke(
        path: Path,
        cacheKeys: CacheKeys,
        converter: Converter.Factory<T>,
        execute: Execute<T>,
        source: Source<T>,
        sink: Sink<T>,
    ): Flow<T>

}