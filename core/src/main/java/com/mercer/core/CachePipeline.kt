package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/7/28
 * desc:
 *   进行缓存的管道
 */
interface CachePipeline<T> {

    suspend fun read(path: Path): T?

    suspend fun write(path: Path, value: T?)

}