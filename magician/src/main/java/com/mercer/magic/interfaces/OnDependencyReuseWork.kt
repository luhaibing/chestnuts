package com.mercer.magic.interfaces

import org.gradle.api.Project

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   本地模块编译优化速度插件 的具体实现逻辑
 */
interface OnDependencyReuseWork {

    fun apply(target: Project)

}