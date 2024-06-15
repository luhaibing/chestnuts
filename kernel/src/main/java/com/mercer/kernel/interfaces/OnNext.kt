package com.mercer.kernel.interfaces

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   获取下一个
 */
interface OnNext<T> {

    /**
     * 下一个
     */
    fun next(): T
}