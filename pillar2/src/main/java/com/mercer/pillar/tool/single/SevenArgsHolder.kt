@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class SevenArgsHolder<out T, in A, in B, in C, in D, in E, in F, in G>(
    creator: (A, B, C, D, E, F, G) -> T
) {

    private var creator: ((A, B, C, D, E, F, G) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c, d, e, f, g).apply {
                instance = this
            }
        }
    }

}
