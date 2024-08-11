package com.mercer.core

/**
 * author:  Mercer
 * date:    2024/8/6
 * desc:
 *   标记
 */
object Flag {
    const val FLAG_NONE = 0B0

    const val FLAG_PUT = 0B1
    const val FLAG_DELETE = FLAG_PUT shl 1
    const val FLAG_POST = FLAG_DELETE shl 1
    const val FLAG_GET = FLAG_POST shl 1

    const val FLAG_FORM = FLAG_GET shl 1
    const val FLAG_MULTIPART = FLAG_FORM shl 1
}