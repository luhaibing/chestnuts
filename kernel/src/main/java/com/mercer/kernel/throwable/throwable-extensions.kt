package com.mercer.kernel.throwable

import kotlin.reflect.KClass


/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   异常相关扩展
 */
operator fun <E : Throwable> CombinedException.get(value: KClass<E>): E? {
    var current = this
    while (true) {
        current.element[value]?.let { return it }
        val next = current.left
        if (next is CombinedException) {
            current = next
        } else {
            return next[value]
        }
    }
}

fun <E : Throwable> CombinedException.getInstance(value: KClass<E>): E? {
    var current = this
    while (true) {
        current.element.getInstance(value)?.let { return it }
        val next = current.left
        if (next is CombinedException) {
            current = next
        } else {
            return next.getInstance(value)
        }
    }
}

fun <E> CombinedException.fold(initial: E, operation: (E, Throwable) -> E): E {
    return operation(left.fold(initial, operation), element)
}

fun CombinedException.minusKey(key: KClass<out Throwable>): Throwable {
    element[key]?.let { return left }
    return when (val newLeft = left.minusKey(key)) {
        null -> element
        left -> this
        else -> CombinedException(newLeft, element)
    }
}

///////////////////////////////////////////////////

operator fun <E : Throwable> Throwable.get(value: KClass<E>): E? {
    return if (this is CombinedException) {
        get(value)
    } else {
        @Suppress("UNCHECKED_CAST")
        if (value == this::class) this as E else null
    }
}

fun <E : Throwable> Throwable.getInstance(value: KClass<E>): E? {
    return if (this is CombinedException) {
        getInstance(value)
    } else {
        @Suppress("UNCHECKED_CAST")
        return if (value.isInstance(this)) this as E else null
    }
}

fun <E> Throwable.fold(initial: E, operation: (E, Throwable) -> E): E {
    return if (this is CombinedException) {
        fold(initial, operation)
    } else {
        operation(initial, this)
    }
}

fun Throwable.minusKey(key: KClass<out Throwable>): Throwable? {
    return if (this is CombinedException) {
        minusKey(key)
    } else {
        if (key == this::class) null else this
    }
}

///////////////////////////////////////////////////


///////////////////////////////////////////////////

operator fun Throwable.plus(value: Throwable): Throwable {
    return value.fold(this) { acc, element ->
        val removed = acc.minusKey(element::class)
        if (removed === null) {
            element
        } else {
            CombinedException(removed, element)
        }
    }
}
