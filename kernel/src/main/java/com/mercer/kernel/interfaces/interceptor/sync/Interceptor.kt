package com.mercer.kernel.interfaces.interceptor.sync

/**
 * @author :Mercer
 * @Created on 2025/04/12.
 * @Description:
 *   拦截器
 */
interface Interceptor<In, Out> where In : Any, Out : Any? {

    /*
    * 拦截处理
    */
    fun intercept(chain: Chain<In, Out>): Out

    interface Chain<In : Any, Out : Any?> {

        /*
         * 参数
         */
        val input: In

        /*
         * 继续
         */
        fun proceed(value: In): Out

    }

}