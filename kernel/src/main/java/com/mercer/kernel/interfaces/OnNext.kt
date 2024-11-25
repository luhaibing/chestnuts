package com.mercer.kernel.interfaces

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   获取下一个
 */
interface OnNext<T> where T : Any? {
    /*
     * 下一个
     */
    fun next(): T
}