package com.mercer.magic.interfaces.impls

import com.mercer.magic.BUNDLE_TASK_REGEX
import com.mercer.magic.PUBLISH_TASK_REGEX
import com.mercer.magic.afterEvaluate
import com.mercer.magic.androidRes
import com.mercer.magic.beans.ModuleRes
import com.mercer.magic.beans.Named
import com.mercer.magic.configure
import com.mercer.magic.createPublication
import com.mercer.magic.extensions.DependencyReusePluginExtension
import com.mercer.magic.interfaces.OnDependencyReuseWork
import com.mercer.magic.javaRes
import com.mercer.magic.maven
import com.mercer.magic.named
import com.mercer.magic.onEach
import com.mercer.magic.record
import com.mercer.magic.requireExtensionNotNull
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.component.LibraryComponentSelector
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.component.ProjectComponentSelector
import org.gradle.api.publish.PublishingExtension
import java.io.File

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   apg7 实现
 */
open class DependencyReuseGradleAGP7Impl : OnDependencyReuseWork {

    override fun apply(target: Project) {
        val rootProject = target.rootProject
        val gradle = rootProject.gradle
        val taskNames = gradle.startParameter.taskNames
        println("gradle start ： ${taskNames.joinToString(",", prefix = "[", postfix = "]")}")
        rootProject.extensions.create("dependencyReuse", DependencyReusePluginExtension::class.java)
        val subprojects = rootProject.subprojects.toList()
        subprojects.onEach {
            replaceModule()
        }
        subprojects.afterEvaluate {
            val extension = rootProject.requireExtensionNotNull()
            val res = project2res(extension)
            if (res != null) {
                publishModule(res, extension)
            }
            // replaceModule(extension)
        }
    }

    private fun Project.replaceModule() {
        configurations.configureEach { configuration ->
            val extension = rootProject.requireExtensionNotNull()
            /*
            configuration.resolutionStrategy.dependencySubstitution { substitutions: DependencySubstitutions ->
                substitutions.all { substitution: DependencySubstitution ->
                    ...
                }
            }
            */
            configuration.resolutionStrategy.dependencySubstitution.all { substitution: DependencySubstitution ->
                val component = substitution.requested
                if (component is ProjectComponentSelector) {
                    val find = rootProject.project(component.projectPath)
                    val named = find.named
                    val res = find.project2res(extension)
                    if (res?.exists == true) {
                        val notation = arrayOf(extension.groupId, res.named.artifactName, res.lastModifiedTime).joinToString(":")
                        println("$name 替换本地模块依赖 >>> ${named.path} 存在缓存 >>> $notation.")
                        substitution.useTarget(notation)
                    } else {
                        System.err.println("$name 替换本地模块依赖 >>> ${named.path} 不存在本地依赖的缓存.")
                    }
                } else if (component is ModuleComponentSelector) {
                    if (component.group == "com.mercer") {
                        val projectPath = component.module.split("_").joinToString(":")
                        val find = try {
                            rootProject.project(projectPath)
                        } catch (e: Exception) {
                            null
                        }
                        if (find?.project2res(extension)?.exists != true && find != null) {
                            substitution.useTarget(find)
                        }
                    }
                } else if (component is LibraryComponentSelector) {
                    // 该种类已弃用.
                } else {
                    // 未知类型的依赖
                }
            }
        }
    }

    private fun Project.replaceModule(extension: DependencyReusePluginExtension) {
        val name = name
        configurations.configureEach { configuration ->
            /*
            configuration.resolutionStrategy.dependencySubstitution { substitutions: DependencySubstitutions ->
                substitutions.all { substitution: DependencySubstitution ->
                    ...
                }
            }
            */
            configuration.resolutionStrategy.dependencySubstitution.all { substitution: DependencySubstitution ->
                val component = substitution.requested
                if (component is ProjectComponentSelector) {
                    val find = rootProject.project(component.projectPath)
                    val named = find.named
                    val res = find.project2res(extension)
                    if (res?.exists == true) {
                        val notation = arrayOf(extension.groupId, res.named.artifactName, res.lastModifiedTime).joinToString(":")
                        println("$name 替换本地模块依赖 >>> ${named.path} 存在缓存 >>> $notation.")
                        substitution.useTarget(notation)
                    } else {
                        System.err.println("$name 替换本地模块依赖 >>> ${named.path} 不存在本地依赖的缓存.")
                    }
                } else if (component is ModuleComponentSelector) {
                    if (component.group == "com.mercer") {
                        val projectPath = component.module.split("_").joinToString(":")
                        val find = try {
                            rootProject.project(projectPath)
                        } catch (e: Exception) {
                            null
                        }
                        if (find?.project2res(extension)?.exists != true && find != null) {
                            substitution.useTarget(find)
                        }
                    }
                } else if (component is LibraryComponentSelector) {
                    // 该种类已弃用.
                } else {
                    // 未知类型的依赖
                }
            }
        }
    }

    private fun Project.project2res(extension: DependencyReusePluginExtension): ModuleRes? {
        if (project.buildFile.exists().not()) {
            return null
        }
        if (pluginManager.hasPlugin("com.android.application")) {
            return null
        }
        if (pluginManager.hasPlugin("maven-publish")) {
            // return null
        }
        val res = if (pluginManager.hasPlugin("com.android.library")) {
            androidRes()
        } else if (pluginManager.hasPlugin("java") || pluginManager.hasPlugin("kotlin")) {
            javaRes()
        } else if (pluginManager.hasPlugin("com.android.application")) {
            null
        } else {
            System.err.println("模块 $path 类型错误,无法获取生成依赖信息的快照.")
            null
        } ?: return null
        val path = arrayOf(extension.groupId.replace(".", File.separator), named.artifactName, res.lastModifiedTime).joinToString(File.separator)
        return res.copy(exists = File(extension.respFile, path).exists())
    }

    private fun Project.publishModule(res: ModuleRes, extension: DependencyReusePluginExtension) {
        val group = extension.groupId
        val respFile = extension.respFile
        val artifact = project.named.artifactName
        pluginManager.apply("maven-publish")
        afterEvaluate {
            extensions.configure<PublishingExtension> {
                maven(respFile.toURI())
                val components = project.components
                createPublication("dev") {
                    groupId = group
                    artifactId = artifact
                    version = res.lastModifiedTime
                    from(if ("java" in components.names) components.getByName("java") else components.getByName("debug"))
                }
            }
            val recordTask = tasks.create("record") {
                it.group = "publishing"
                it.doLast {
                    record(res, respFile)
                }
            }
            tasks.matching {
                it.group == "publishing" && PUBLISH_TASK_REGEX.matches(it.name)
            }.configureEach {
                it.dependsOn(recordTask)
            }
            val path = arrayOf(group.replace(".", File.separator), res.named.artifactName, res.lastModifiedTime).joinToString(File.separator)
            // 简单的认为只要目录文件地址存在,依赖就存在,不具体检测文件夹下的文件的正确性
            val publishTask = tasks.findByName("publishDevPublicationToMavenRepository")
            if (File(respFile, path).exists().not() && publishTask != null && extension.autoPublish) {
                when (res.type) {
                    ModuleRes.Type.Java -> tasks.findByName("jar")?.finalizedBy(publishTask)
                    ModuleRes.Type.Android -> {
                        tasks.matching {
                            BUNDLE_TASK_REGEX.matches(it.name)
                        }.configureEach {
                            it.finalizedBy(publishTask)
                        }
                    }
                }
            }
        }
    }

}