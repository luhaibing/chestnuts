package com.mercer.paper.elements

import com.mercer.paper.Paper
import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   特征/标识
 */
sealed interface Character<out E : Paper> : PaperContext.Element {

    companion object : PaperContext.Key<Character<*>>

    override val key: PaperContext.Key<*>
        get() = Character

}

data class Page(
    val value: Long
) : Character<Paper> {
    companion object {
        // 默认
        val DEFAULT = Page(0)
        // 增长
        val INCREASE = Page(-1)
    }
}

/**
 * 唯一
 */
interface Tag<out E : Paper> : Character<E>