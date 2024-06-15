package com.mercer.kernel.interfaces.interceptor

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   拦截器
 */
interface Interceptor<In, Out> where In : Any {

    /**
     * 拦截处理
     */
    suspend fun intercept(chain: Chain<In, Out>): Out

    interface Chain<In, Out> {

        /**
         * 参数
         */
        val input: In

        /**
         * 继续
         */
        suspend fun proceed(value: In): Out

    }

}