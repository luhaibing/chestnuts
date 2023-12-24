package com.mercer.lib.test1

import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val result = testApi.func0("v1=1", "v2=22")
    println(result)
    Unit
}