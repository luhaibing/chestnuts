package com.mercer.test.lib

import okhttp3.Interceptor
import okhttp3.Response

/**
 * author:  Mercer
 * date:    2024/08/10
 * desc:
 *   自定义的拦截器
 */

class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        print("*************")
        print(" ")
        println("MyInterceptor")
        print(" ")
        print("*************")
        println()
        val request = chain.request()
        println()
        println()
        return chain.proceed(request)
    }

}