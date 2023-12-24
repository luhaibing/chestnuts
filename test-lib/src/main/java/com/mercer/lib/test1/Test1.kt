package com.mercer.lib.test1

import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val result = testApi.func1("1", "2", "v3=3", mapOf("v4" to "4", "v5" to "5"))
    println(result)
    Unit
}