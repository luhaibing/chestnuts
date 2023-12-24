package com.mercer.core

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface Creator {

    fun <T : Any> create(service: KClass<T>): T

    fun <T> suspend2flow(block: suspend () -> T): Flow<T>

    fun any2str(value: Any?): String?

}