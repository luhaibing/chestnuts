package com.mercer.paper

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   裁纸刀
 */
interface PaperCutter<out T : Paper> {

    operator fun invoke(paperContext: PaperContext): T

}