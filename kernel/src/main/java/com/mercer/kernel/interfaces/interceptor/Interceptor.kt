package com.mercer.kernel.interfaces.interceptor

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   拦截器
 */
interface Interceptor<In, Out> where In : Any?, Out : Any? {

    /*
     * 拦截处理
     */
    suspend fun intercept(chain: Chain<In, Out>): Out

    interface Chain<In : Any?, Out : Any?> {
        /*
         * 参数
         */
        val input: In?

        /*
         * 继续
         */
        suspend fun proceed(value: In): Out

    }

}