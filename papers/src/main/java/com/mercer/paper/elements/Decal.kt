package com.mercer.paper.elements

import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   贴纸(实例的参数)
 */
interface Decal<E : Any?> : PaperContext.Element {

    override val key: PaperContext.Key<Decal<*>>
        get() = Decal

    companion object : PaperContext.Key<Decal<*>> {
        val DEFAULT = object : Decal<Any?> {
            override val value: Any?
                get() = null
        }
    }

    val value: E

}