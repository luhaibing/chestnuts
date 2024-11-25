package com.mercer.kernel.throwable

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   组合类异常
 */
data class CombinedException(val left: Throwable, val element: Throwable) : AbsException(null) {
    override fun toString(): String {
        return "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"
    }
}