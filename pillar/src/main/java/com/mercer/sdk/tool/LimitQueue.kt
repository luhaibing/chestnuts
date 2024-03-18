package com.mskj.mercer.core.tool

import java.util.*

/**
 * 限定长度的队列
 */
class LimitQueue<E>(
    private val limit: Int,
    private val queue: Queue<E> = LinkedList()
) : Queue<E> by queue {

    //////////////////////////////////////////////////////////////////////////////////////////

    fun limit() = limit

    /**
     * 入队
     * @param e
     */
    override fun offer(e: E): Boolean {
        /*
        if (queue.size >= limit) {
            //如果超出长度,入队时,先出队
            queue.poll()
        }
        return queue.offer(e)
        */
        if (queue.size >= limit) {
            //如果超出长度,入队时,不允许入列
            return false
        }
        return queue.offer(e)
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("LimitQueue{")
        builder.append("limit=")
        builder.append(limit)
        builder.append(",")
        builder.append("[")
        for (e in this) {
            builder.append(e.toString())
            builder.append(",")
        }
        if (size > 0) {
            builder.delete(builder.length - 1, builder.length)
        }
        builder.append("]")
        builder.append("}")
        return builder.toString()
    }

}