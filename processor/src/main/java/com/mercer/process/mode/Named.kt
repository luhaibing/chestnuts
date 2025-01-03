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

        // retrofit2.http.Body
        const val NAME_BODY = TYPE_VARIABLE shl 1

        // retrofit2.http.Path
        const val NAME_PATH = NAME_BODY shl 1

        // 保存 用于缓存的key
        const val NAME_CACHE_KEYS = NAME_PATH shl 1

        // 序列换转换器
        const val NAME_CONVERTER = NAME_CACHE_KEYS shl 1

        // 序列换转换器的值
        const val NAME_CONVERTER_DEFAULT_VALUE_FUNC = NAME_CONVERTER shl 1

        // 持久化具体实现
        const val NAME_PERSISTENCE = NAME_CONVERTER_DEFAULT_VALUE_FUNC shl 1
        // 持久化具体实现

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
