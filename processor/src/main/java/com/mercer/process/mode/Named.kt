package com.mercer.process.mode

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   变量命名
 */
data class Named(
    val value: String,
    val flag: Int
) {
    companion object {
        // 类的属性
        const val TYPE_PROPERTY = 0B1

        // 类的方法
        const val TYPE_FUNCTION = TYPE_PROPERTY shl 1

        // 方法的参数
        const val TYPE_PARAMETER = TYPE_FUNCTION shl 1

        // 方法内的临时变量名
        const val TYPE_TEMPORARY = TYPE_PARAMETER shl 1

        // 方法内的变量名(非临时变量名/参与调用api方法)
        const val TYPE_VARIABLE = TYPE_TEMPORARY shl 1


        const val NAME_BODY = TYPE_VARIABLE shl 1

        const val NAME_PATH = NAME_BODY shl 1

        const val NAME_PIPELINE = NAME_PATH shl 1

        const val NAME_SCOPE = NAME_PIPELINE shl 1

        const val NAME_FLOW = NAME_SCOPE shl 1

        fun produceName(excludes: List<String>, namePrefix: String = "v"): String {
            var position = 1
            while (true) {
                val name = "$namePrefix$position"
                position += 1
                if (name in excludes) {
                    continue
                }
                return name
            }
        }

        fun produce(excludes: List<Named>, namePrefix: String): String {
            return produceName(excludes.map { it.value }, namePrefix)
        }
    }

}
