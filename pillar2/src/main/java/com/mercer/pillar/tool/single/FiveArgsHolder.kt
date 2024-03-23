@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class FiveArgsHolder<out T, in A, in B, in C, in D, in E>(
    creator: (A, B, C, D, E) -> T
) {

    private var creator: ((A, B, C, D, E) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C, d: D, e: E): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c, d, e).apply {
                instance = this
            }
        }
    }

}
