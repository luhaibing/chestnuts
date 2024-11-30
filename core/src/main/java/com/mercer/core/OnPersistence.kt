package com.mercer.core

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   持久化
 */
interface OnPersistence {

    /**
     * 读取/read
     * @param path 路径
     * @param cacheKeys 缓存键
     * @param deserializer 反序列化器
     * @return 读取的数据
     */
    suspend fun <T : Any?> source(path: Path, cacheKeys: CacheKeys, deserializer: Deserializer<T>): T?

    /**
     * 保存/write
     * @param value 要保存的数据
     * @param path 路径
     * @param cacheKeys 缓存键
     * @param serializer 序列化器
     */
    suspend fun <T : Any?> sink(value: T?, path: Path, cacheKeys: CacheKeys, serializer: Serializer<T>)

}