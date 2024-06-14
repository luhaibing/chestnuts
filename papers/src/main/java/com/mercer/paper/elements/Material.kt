package com.mercer.paper.elements

import com.mercer.paper.Paper
import com.mercer.paper.PaperContext

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   材质
 */
interface Material<out E : Paper> : PaperContext.Element {

    companion object : PaperContext.Key<Material<*>> {
       // val DEFAULT = object : Material<Paper> {}
    }

    override val key: PaperContext.Key<Material<*>>
        get() = Material

}