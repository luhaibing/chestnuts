package com.mercer.kernel.throwable

import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2025/04/10.
 * @Description:
 *   组合类异常
 */
data class CombinedException(val left: Throwable, val element: Throwable) : AbsException(null) {

    companion object {
        operator fun invoke(values: List<Throwable>): Throwable? {
            return when (values.size) {
                0 -> null
                1 -> values[0]
                else -> values.reduce { acc, element ->
                    acc + element
                }
            }
        }
    }

    val size: Int
        get() {
            /*
            var size = 1
            var cur: Throwable = this
            while (cur is CombinedException) {
                cur = cur.left
                size++
            }
            return size
            */
            return keys.size
        }

    val keys: List<KClass<out Throwable>>
        get() {
            val keys = mutableListOf<KClass<out Throwable>>()
            var cur: Throwable = this
            while (cur is CombinedException) {
                keys.add(cur.element::class)
                cur = cur.left
            }
            keys.add(cur::class)
            return keys
        }

    override fun toString(): String {
        return "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CombinedException) {
            return false
        }
        val size = size
        if (other.size != size) {
            return false
        }
        val total = keys.mapNotNull { other[it] }.size
        return total == size
    }

    override fun hashCode(): Int {
        return left.hashCode() + element.hashCode()
    }

    operator fun contains(key: KClass<out Throwable>): Boolean {
        return this[key] != null
    }

    operator fun contains(value: Throwable): Boolean {
        return if (value is CombinedException) {
            value.keys.all { this[it] != null }
        } else {
            this[value::class] != null
        }
    }

}