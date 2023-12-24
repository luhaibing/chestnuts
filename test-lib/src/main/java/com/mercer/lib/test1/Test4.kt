package com.mercer.lib.test1

import com.mercer.test.lib.model.Person
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    testApi.func4(
        v1 = Person(name = "Barton Sosa", age = 6536),
        v2 = "233"
    )
    Unit
}