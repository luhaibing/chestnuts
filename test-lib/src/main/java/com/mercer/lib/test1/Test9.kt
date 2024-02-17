package com.mercer.lib.test1

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val result = testApi.func9(
        "https://192.168.0.1:8080/test999",
         "v1.0.0",
    )
    println(result)
    Unit
}