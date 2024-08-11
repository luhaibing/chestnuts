package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/4/3
 * desc:
 *   请求路径
 */

sealed interface Path {

    val value: String
    val flag: Int

    @JvmInline
    value class PUT(override val value: String) : Path {
        override val flag: Int get() =  Flag.FLAG_PUT
    }

    @JvmInline
    value class DELETE(override val value: String) : Path{
        override val flag: Int get() =  Flag.FLAG_DELETE
    }

    @JvmInline
    value class POST(override val value: String) : Path{
        override val flag: Int get() =  Flag.FLAG_POST
    }

    @JvmInline
    value class GET(override val value: String) : Path{
        override val flag: Int get() =  Flag.FLAG_GET
    }

    /*
    @JvmInline
    value class PATCH(override val value: String) : Path
    @JvmInline
    value class HEAD(override val value: String) : Path
    @JvmInline
    value class OPTIONS(override val value: String) : Path
    @JvmInline
    value class HTTP(override val value: String) : Path
    */
}
