package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/8/9
 * desc:
 *   Append 注解的参数,用于提供 追加参数的值
 */
// 简单模式 Simpleness
// 复杂模式 Completeness
interface Provider {
    fun provide(path: Path, key: String): Any?
}