package com.mercer.core

interface Provider {
    fun provide(vararg args: Argument<*>): Any?
}