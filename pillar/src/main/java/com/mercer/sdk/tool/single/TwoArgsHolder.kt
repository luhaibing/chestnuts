@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class TwoArgsHolder<out T, in A, in B>(creator: (A, B) -> T) {

    private var creator: ((A, B) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A, b: B): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a, b).apply {
                instance = this
            }
        }
    }

}