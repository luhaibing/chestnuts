package com.mercer.magic

import com.mercer.magic.interfaces.impls.DependencyReuseGradleAGP7Impl
import com.mercer.magic.interfaces.impls.DependencyReuseGradleAGP8Impl
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   本地模块编译优化速度插件
 */
class ProjectDependencyCompilerOptimizationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val gradleVersion = target.gradle.gradle.gradleVersion.split(".").firstOrNull()?.toInt() ?: 7
        val work = if (gradleVersion < 8) {
            DependencyReuseGradleAGP7Impl()
        } else {
            DependencyReuseGradleAGP8Impl()
        }
        work.apply(target)
    }

}