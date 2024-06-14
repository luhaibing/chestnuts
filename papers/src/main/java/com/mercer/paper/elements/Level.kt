package com.mercer.paper.elements

import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   层级
 */
// TODO: 暂无应用
data class Level(
    val value: Int
) : PaperContext.Element {
    companion object : PaperContext.Key<Level> {
        val DEFAULT = Level(1)
    }
    override val key = Level
}