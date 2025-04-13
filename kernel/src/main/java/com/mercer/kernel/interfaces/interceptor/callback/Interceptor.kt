package com.mercer.kernel.interfaces.interceptor.callback

/**
 * @author :Mercer
 * @Created on 2025/04/12.
 * @Description:
 *   拦截器
 */
interface Interceptor<In> where In : Any {

    /**
     * 拦截处理
     */
    fun intercept(chain: Chain<In>)

    interface Chain<In> where In : Any {

        /**
         * @param   position 当前拦截器的位置
         * @param   interceptors 拦截器
         */
        data class Context<In>(
            val position: Int,
            val interceptors: List<Interceptor<In>>
        ) where In : Any

        /*
         * 参数
         */
        val input: In

        /**
         * 中断传递
         */
        fun interrupt(value: Exception?)

        /**
         * 继续
         */
        fun proceed(value: In)

    }

}