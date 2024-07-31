package com.mercer.process.mode

import com.mercer.core.Mode
import com.squareup.kotlinpoet.TypeName


/**
 * @author :Mercer
 * @Created on 2024/7/30.
 * @Description:
 *
 */
data class CacheBean(
    val pipeline: TypeName,
    val mode: Mode
)