package com.mercer.library.test

import okhttp3.Interceptor
import okhttp3.Response

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
        // return chain.proceed(chain.request())
        TODO("Not yet implemented")
    }

}