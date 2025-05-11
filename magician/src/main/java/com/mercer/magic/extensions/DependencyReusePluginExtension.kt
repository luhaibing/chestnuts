package com.mercer.magic.extensions

import java.io.File
import java.net.URI
import kotlin.properties.Delegates

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   本地模块编译优化速度插件的扩展参数
 */
open class DependencyReusePluginExtension {

    // 本地依赖的组名
    var groupId: String by Delegates.notNull()

    // 仓库地址
    var respUri: URI by Delegates.notNull()

    // 仓库地址
    var respFile: File
        get() {
            return File(respUri)
        }
        set(value) {
            respUri = value.toURI()
        }

    // 是否启用
    var enabled: Boolean = true

    // 是否随任务自动发布
    var autoPublish: Boolean = true

}