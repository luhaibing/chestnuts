package com.mercer.process.mode

import com.google.devtools.ksp.symbol.ClassKind
import com.mercer.core.Mode
import com.squareup.kotlinpoet.TypeName

/**
 * author:  Mercer
 * date:    2024/8/4
 * desc:
 *   缓存相关
 */
data class CacheBean(
    val mode: Mode,
    val pipeline: TypeName,
    val pipelineFunctionReturn: TypeName,
    val classKind: ClassKind,
    val named: Named
)