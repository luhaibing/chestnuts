package com.mercer.core

/**
 * author:  mercer
 * date:    2024/3/3 02:08
 * desc:
 *   请求路径
 */
sealed interface Path {
    val value: String

    @JvmInline
    value class PUT(override val value: String) : Path
    @JvmInline
    value class DELETE(override val value: String) : Path
    @JvmInline
    value class POST(override val value: String) : Path
    @JvmInline
    value class GET(override val value: String) : Path

    @JvmInline
    value class PATCH(override val value: String) : Path
    @JvmInline
    value class HEAD(override val value: String) : Path
    @JvmInline
    value class OPTIONS(override val value: String) : Path
    @JvmInline
    value class HTTP(override val value: String) : Path

}
