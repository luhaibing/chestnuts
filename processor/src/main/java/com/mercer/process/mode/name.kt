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

        // 需要转换 toString
        const val TO_JSON = TEMPORARY shl 1

        fun produce(excludes: List<Named>): String {
            val names = excludes.map { it.value }
            var position = 1
            while (true) {
                val name = "v$position"
                position += 1
                if (name in names) {
                    continue
                }
                return name
            }
        }

    }
}