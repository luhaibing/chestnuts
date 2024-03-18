@file:Suppress("unused")

package com.mskj.mercer.core.tool.single

open class OneArgsHolder<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    operator fun invoke(a: A): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(a).apply {
                instance = this
            }
        }
    }

}