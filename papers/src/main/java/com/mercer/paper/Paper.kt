package com.mercer.paper

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *   纸张实例
 */
interface Paper {

    /**
     * 上下文
     */
    val paperContext: PaperContext

    /**
     * 显示
     */
    fun show()

    /**
     * 隐藏
     */
    fun dismiss()

    /**
     * 渲染视图
     */
    // fun render()

}