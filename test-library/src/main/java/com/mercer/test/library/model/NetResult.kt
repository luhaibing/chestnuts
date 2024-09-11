package com.mercer.test.library.model

data class NetResult<T>(
    val code: Int,
    val data: T?,
    val message: String
)
