package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/8/9
 * desc:
 *   缓存相关
 */

// 数据来源
sealed interface Source {
    // 缓存
    data object Cache : Source

    // 网络
    data object Network : Source
}

// 数据
internal data class Result<out T>(
    val value: T,
    val source: Source
)

// 缓存的模式
@Suppress("unused")
enum class Strategy {
    // 先尝试读取缓存,如果缓存不为空,就先发送一次,再进行网络请求
    DEFAULT,
    // 读取缓存和网络请求同时开始,哪个先返回,先使用哪个,如果先获取到是网络响应就直接发送,如果是缓存那就再进行网络请求
    SELECT,
}

// 进行缓存的管道
interface Pipeline<T> {

    // 读取
    suspend fun read(path: Path): T?

    // 写入
    suspend fun write(path: Path, value: T?)

}