package com.mercer.core

/**
 * @author :Mercer
 * @Created on 2024/11/24.
 * @Description:
 *   缓存使用的键的集合
 */
class CacheKeys : MutableMap<String, Any> by mutableMapOf() {
    override fun toString(): String {
        val ks = keys.sorted()
        return ks.joinToString("&") {
            arrayOf(it, get(it).toString()).joinToString("=")
        }
    }
}