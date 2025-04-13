package com.mercer.kernel.throwable

import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2025/04/10.
 * @Description:
 *   组合异常相关扩展
 */

/////////////////////////////////////////////////// 增

operator fun Throwable.plus(value: Throwable): Throwable {
    return value.fold(this) { acc, element ->
        val key = element::class
        val removed = acc.minusKey(key)         // 尝试从 acc 中移除 key 得到移除后的结果
        if (removed === null) {                       // 结果为 null 说明,acc 的类型是 key
            element                            // 返回 element
        } else {                                      // 否则
            CombinedException(removed, element)// 返回 组合值
        }
    }
}

/////////////////////////////////////////////////// 减

fun <E : Throwable> Throwable.minusKey(key: KClass<E>): Throwable? {
    return if (this is CombinedException) { // 如果是 CombinedException
        this.minusKey(key)                          // 就使用 CombinedException 的减法
    } else if (this::class == key) {        // 对应类型是 key
        null                                // 返回 null
    } else {                                // 否则
        this                                // 返回自己
    }
}

fun <E : Throwable> CombinedException.minusKey(key: KClass<E>): Throwable {
    element[key]?.let { return left }                       // 从 left 中查询
    return when (val newLeft = left.minusKey(key)) {  // 尝试 从 left 中移除 key
        null -> element                                     // left 的类型 就是 key
        left -> this                                        // left 及其更左边 的类型 都不是 key
        else -> CombinedException(newLeft, element)         // left 及其更左边 的类型 存在 key
    }
}

/////////////////////////////////////////////////// 取

@Suppress("UNCHECKED_CAST")
operator fun <E : Throwable> Throwable.get(key: KClass<E>): E? {
    return if (this is CombinedException) {          // 如果是 CombinedException
        this[key]                                    // 使用 CombinedException 的查询
    } else if (this::class == key) {                 // 对应类型是 key
        this as E                                    // 返回自己
    } else {                                         // 否则
        null                                         // 返回 null
    }
}

operator fun <E : Throwable> CombinedException.get(key: KClass<E>): E? {
    var current = this
    while (true) {
        current.element[key]?.let { return it }         // 从 left 中查询
        val next = current.left
        if (next is CombinedException) {                // 如果是 CombinedException
            current = next
        } else {                                        // 否则
            return next[key]                            // 使用 Throwable 的查询
        }
    }
}

/*
fun Throwable.getInstances(key: KClass<out Throwable>): Throwable? {
    return if (this is CombinedException) {
        getInstances(key)
    } else if (key.isInstance(this)) {
        this
    } else {
        null
    }
}

fun CombinedException.getInstances(key: KClass<out Throwable>): Throwable? {
    val result = arrayListOf<Throwable>()
    for (k in keys) {
        val v = get(k) ?: continue
        if (key.isInstance(v)) {
            result.add(v)
        }
    }
    return CombinedException(result)
}
*/

/////////////////////////////////////////////////// 遍历

fun <E> Throwable.fold(initial: E, operation: (E, Throwable) -> E): E {
    return if (this is CombinedException) {             // 如果是 CombinedException
        fold(initial, operation)                        // 使用 CombinedException 的遍历
    } else {                                            // 否则
        operation(initial, this)                        // 调用 operation 进行操作
    }
}

fun <E> CombinedException.fold(initial: E, operation: (E, Throwable) -> E): E {
    val result = left.fold(initial, operation)        // 使用 Throwable 的遍历
    return operation(result, element)                      // 调用 operation 进行操作
}

fun main() {
    val v1 = NullPointerException()
    val v2 = NullPointerException()
    val v3 = IllegalArgumentException()
    val v4 = IllegalArgumentException()
    val v5 = RuntimeException()
    val v6 = v1 + v3
    val v7 = v4 + v5
    val v8 = v6 + v5
    val v9 = v5 + v6
    val v10 = v6 + v7
    val v11 = v1 + v2 + v3 + v4 + v5
    println(v8 == v9)
    /*
    val instances = v11.getInstances(RuntimeException::class)
    if (instances is CombinedException) {
        var throwable: Throwable? = v11
        for (key in instances.keys) {
            throwable = throwable?.minusKey(key)
            println(throwable)
        }
    } else {
        throw NullPointerException()
    }
    println(instances)
    */
}