package com.mercer.kernel.throwable

import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   组合异常/复合异常
 */

/**
 *   组合类异常
 *   本身没有意义,重要的是 values 携带的值
 */
/*
class CombinedException(
    val values: List<Exception>,
    reason: Throwable? = null,
) : AbsException(reason) {
    fun expand(): List<Throwable> {
        return arrayListOf<Throwable>().apply {
            for (value in values) {
                if (value is InherentThrowable) {
                    value()
                } else {
                    value
                }.let {
                    if (it is CombinedException) {
                        addAll(it.expand())
                    } else {
                        add(it)
                    }
                }
            }
        }
    }
}
*/

class CombinedException(
    val left: Throwable,
    val element: Throwable
) : AbsException(null)


operator fun Throwable.plus(value: Throwable): CombinedException {
    value.fold(this) { acc, element ->
        println()
        println()
        println()
        TODO()
    }
    TODO()
}

fun <R> Throwable.fold(initial: R, operation: (R, Throwable) -> R): Throwable {
    // return operation(left.fold(initial, operation), element)
    TODO()
}

inline operator fun <reified T : Throwable> Throwable.minus(value: T): Throwable {
    TODO("Not yet implemented")
}

inline operator fun <reified T : Throwable> Throwable.get(key: KClass<T>): T? {
    return get(key.java)
}

inline operator fun <reified T : Throwable> Throwable.get(key: Class<T>): T? {
    TODO("Not yet implemented")
}
