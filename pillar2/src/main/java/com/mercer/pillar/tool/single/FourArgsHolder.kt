@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class FourArgsHolder<out T, in A, in B, in C, in D>(
    creator: (A, B, C, D) -> T
) {

    private var creator: ((A, B, C, D) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C, d: D): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c, d).apply {
                instance = this
            }
        }
    }

}