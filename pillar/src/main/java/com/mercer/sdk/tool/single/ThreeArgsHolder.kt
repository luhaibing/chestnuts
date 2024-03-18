@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class ThreeArgsHolder<out T, in A, in B, in C>(
    creator: (A, B, C) -> T
) {

    private var creator: ((A, B, C) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c).apply {
                instance = this
            }
        }
    }

}