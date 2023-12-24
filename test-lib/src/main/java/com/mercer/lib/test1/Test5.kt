package com.mercer.lib.test1

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    testApi.func5(
        1.toString(),
        2,
        3
    )
    Unit
}