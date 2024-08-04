package com.mercer.process.mode

/**
 * author:  mercer
 * date:    2024/2/15 09:05
 * desc:
 *   函数内部的变量命名
 */
data class Named(
    val value: String,
    val flag: Int
) {
    companion object {

        // 参数
        const val PARAMETER = 1

        // 变量
        const val VARIABLE = PARAMETER shl 1

        // 临时
        const val TEMPORARY = VARIABLE shl 1

        //全局变量
        const val GLOBAL_VARIABLE = TEMPORARY shl 1

        // [特有属性] path
        const val PATH_NAME = GLOBAL_VARIABLE shl 1

        // [特有属性] pipeline
        const val PIPELINE_NAME = PATH_NAME shl 1

        fun produce(excludes: List<Named>, namePrefix: String = "v"): String {
            val names = excludes.map { it.value }
            var position = 1
            while (true) {
                val name = "$namePrefix$position"
                position += 1
                if (name in names) {
                    continue
                }
                return name
            }
        }

    }
}