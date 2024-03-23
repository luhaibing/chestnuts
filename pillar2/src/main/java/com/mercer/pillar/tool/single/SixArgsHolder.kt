@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class SixArgsHolder<out T, in A, in B, in C, in D, in E, in F>(
    creator: (A, B, C, D, E, F) -> T
) {

    private var creator: ((A, B, C, D, E, F) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c, d, e, f).apply {
                instance = this
            }
        }
    }

}
