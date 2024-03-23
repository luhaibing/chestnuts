@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class NineArgsHolder<out T, in A, in B, in C, in D, in E, in F, in G, in H, in I>(
    creator: (A, B, C, D, E, F, G, H, I) -> T
) {

    private var creator: ((A, B, C, D, E, F, G, H, I) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b, c, d, e, f, g, h, i).apply {
                instance = this
            }
        }
    }

}