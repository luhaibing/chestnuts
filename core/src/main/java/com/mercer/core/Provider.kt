package com.mercer.core

// 简单模式 Simpleness
// 复杂模式 Completeness
interface Provider {
    fun provide(path: Path, key: String): Any?
}