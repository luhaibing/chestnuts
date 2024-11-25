package com.mercer.kernel.interfaces

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   节点
 */

interface Node<T> {

    interface Key<E>

    operator fun plus(context: Node<T>): Node<T> {
        val fold = context.fold(this) { acc, element ->
            val removed = acc.minusKey(element.key)
            if (removed === null) {
                element
            } else {
                CombinedNode(removed, element)
            }
        }
        return fold
    }

    fun minusKey(key: Key<*>): Node<T>?

    operator fun <E> get(key: Key<E>): E?

    fun <R> fold(initial: R, operation: (R, Element<T>) -> R): R

    interface Element<T> : Node<T> {

        val key: Key<*>

        override fun <E> get(key: Key<E>): E? {
            @Suppress("UNCHECKED_CAST")
            return if (this.key == key) {
                this as E
            } else {
                null
            }
        }

        override fun <R> fold(initial: R, operation: (R, Element<T>) -> R): R =
            operation(initial, this)

        override fun minusKey(key: Key<*>): Element<T>? {
            return if (this.key == key) {
                null
            } else {
                this
            }
        }

    }

    class CombinedNode<T>(
        private val left: Node<T>,
        private val element: Element<T>
    ) : Node<T> {

        override fun <E> get(key: Key<E>): E? {
            var cur = this
            while (true) {
                cur.element[key]?.let {
                    return@let it
                }
                val next = cur.left
                if (next is CombinedNode) {
                    cur = next
                } else {
                    return next[key]
                }
            }
        }

        override fun <R> fold(initial: R, operation: (R, Element<T>) -> R): R {
            // 关键点:递归调用
            return operation(left.fold(initial, operation), element)
        }

        override fun minusKey(key: Key<*>): Node<T> {
            element[key]?.let {
                return@let left
            }
            val newLeft = left.minusKey(key)
            return when {
                newLeft == null -> {
                    element
                }

                newLeft === left -> {
                    this
                }

                else -> {
                    CombinedNode(newLeft, element)
                }
            }
        }

        private fun size(): Int {
            var cur = this
            var size = 2
            while (true) {
                cur = cur.left as? CombinedNode ?: return size
                size++
            }
        }

        private fun contains(element: Element<*>): Boolean = get(element.key) == element

        private fun containsAll(context: CombinedNode<*>): Boolean {
            var cur = context
            while (true) {
                if (!contains(cur.element)) {
                    return false
                }
                val next = cur.left
                if (next is CombinedNode<*>) {
                    cur = next
                } else {
                    @Suppress("UNCHECKED_CAST")
                    return contains(next as Element<T>)
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            return this === other || other is CombinedNode<*> && other.size() == size() && other.containsAll(this)
        }

        override fun hashCode(): Int {
            return left.hashCode() + element.hashCode()
        }

        override fun toString(): String {
            return "[" + fold("") { acc, element ->
                if (acc.isEmpty()) {
                    element.toString()
                } else {
                    "$acc, $element"
                }
            } + "]"
        }

    }

}