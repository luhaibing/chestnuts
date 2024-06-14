package com.mercer.paper

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   纸张上下文
 */
interface PaperContext {

    interface Key<E : Element>

    /**
     * 具体上下文的超类
     */
    interface Element : PaperContext {

        val key: Key<*>

        override operator fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as E else null

        override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        override fun minusKey(key: Key<*>): PaperContext =
            if (this.key == key) Empty else this

    }

    /**
     * 上下文聚合
     */
    data class Combined(
        val left: PaperContext,
        val element: Element
    ) : PaperContext {

        override fun minusKey(key: Key<*>): PaperContext {
            element[key]?.let { return left }
            val newLeft = left.minusKey(key)
            return when {
                newLeft === Empty -> element
                newLeft === left -> this
                else -> Combined(newLeft, element)
            }
        }

        override fun <R> fold(initial: R, operation: (R, Element) -> R): R {
            return operation(left.fold(initial, operation), element)
        }

        override fun <E : Element> get(key: Key<E>): E? {
            var cur = this
            while (true) {
                cur.element[key]?.let { return it }
                val next = cur.left
                if (next is Combined) {
                    cur = next
                } else {
                    return next[key]
                }
            }
        }

        ///////////////////////////////////
        private fun size(): Int {
            var cur = this
            var size = 2
            while (true) {
                cur = cur.left as? Combined ?: return size
                size++
            }
        }

        private fun contains(element: Element): Boolean =
            get(element.key) == element

        private fun containsAll(context: Combined): Boolean {
            var cur = context
            while (true) {
                if (!contains(cur.element)) return false
                val next = cur.left
                if (next is Combined) {
                    cur = next
                } else {
                    return contains(next as Element)
                }
            }
        }

        override fun equals(other: Any?): Boolean =
            this === other || other is Combined && other.size() == size() && other.containsAll(this)

        override fun hashCode(): Int = left.hashCode() + element.hashCode()

        override fun toString(): String =
            "[" + fold("") { acc, element ->
                if (acc.isEmpty()) element.toString() else "$acc, $element"
            } + "]"

    }

    /**
     * 空锚点
     */
    object Empty : PaperContext {

        override fun plus(context: PaperContext): PaperContext = context

        override fun minusKey(key: Key<*>): PaperContext = this

        override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial

        override fun <E : Element> get(key: Key<E>): E? = null

    }

    /**
     * 增
     */
    operator fun plus(context: PaperContext): PaperContext {
        return if (context == Empty) {
            this
        } else {
            context.fold(this) { acc, element ->
                val removed = acc.minusKey(element.key)
                if (removed === Empty) {
                    element
                } else {
                    Combined(removed, element)
                }
            }
        }
    }

    /**
     * 删
     */
    fun minusKey(key: Key<*>): PaperContext

    /**
     * 累计
     */
    fun <R> fold(initial: R, operation: (R, Element) -> R): R

    /**
     * 查
     */
    operator fun <E : Element> get(key: Key<E>): E?

}