package com.mercer.library.model

data class NetResult<T>(
    val code: Int,
    val data: T?,
    val message: String
)
