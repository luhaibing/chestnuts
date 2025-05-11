package com.mercer.magic

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.mercer.magic.beans.FileRes
import com.mercer.magic.beans.ModuleRes
import com.mercer.magic.beans.Named
import com.mercer.magic.extensions.DependencyReusePluginExtension
import okio.Buffer
import okio.sink
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   工具方法
 */
/**
 * 请求项目的扩展参数
 */
fun Project.requireExtensionNotNull(): DependencyReusePluginExtension {
    val extension = extensions.findByType(
        DependencyReusePluginExtension::class.java
    ) ?: throw IllegalArgumentException("未指定本地模块依赖复用的插件的参数.")
    //
    try {
        extension.groupId
    } catch (e: Exception) {
        // e.printStackTrace()
        throw IllegalArgumentException("未指定本地模块依赖复用的插件的 groupId.")
    }
    try {
        extension.respUri
    } catch (e: Exception) {
        // e.printStackTrace()
        throw IllegalArgumentException("未指定本地模块依赖复用的插件的仓库地址.")
    }
    return extension
}

/**
 * 项目的名字信息
 */
val Project.named: Named
    get() {
        /*
        // 1
        var cur: Project? = project
        val rootProject = project.rootProject
        val values = arrayListOf<CharSequence>()
        while (cur != null && cur != rootProject) {
            values.add(cur.name)
            cur = cur.parent
        }
        val artifactName = values.reversed().joinToString("_")
        // 2
        val artifactName =  project.path.split(":").filter(String::isNotEmpty).joinToString("_")
        */
        return Named(name = project.name, path = project.path)
    }

/**
 * 项目的本地模块依赖的名字集合
 */
fun Project.projectDependencyNames(): Collection<Named> = project.configurations
    .asSequence()
    .filter {
        CONFIGURATION_REGEX.matches(it.name)
    }
    .map { con ->
        con.dependencies.filterIsInstance<ProjectDependency>()
    }
    .flatten()
    .map(ProjectDependency::getDependencyProject)
    .map(Project::named)
    .toSet()

fun Project.javaRes(): ModuleRes {
    val files = arrayListOf<FileRes>()
    val subProjects = arrayListOf<Named>()
    val javaExtension = extensions.findByName("sourceSets") as? SourceSetContainer
    val kotlinExtension = extensions.findByName("kotlin") as? KotlinProjectExtension
    val javaPredicate: (SourceSet) -> Unit = {
        files.addAll(it.java.files.map(FileRes::Java))
        files.addAll(it.resources.files.map(FileRes::Resources))
    }
    val kotlinPredicate: (KotlinSourceSet) -> Unit = {
        files.addAll(it.kotlin.files.map(FileRes::Kotlin))
        files.addAll(it.resources.files.map(FileRes::Resources))
    }
    if (project.buildFile.exists()) {
        files.add(FileRes.Build(project.buildFile))
    }
    javaExtension?.findByName(MAIN)?.let(javaPredicate)
    javaExtension?.findByName(DEBUG)?.let(javaPredicate)
    kotlinExtension?.sourceSets?.findByName(MAIN)?.let(kotlinPredicate)
    kotlinExtension?.sourceSets?.findByName(DEBUG)?.let(kotlinPredicate)
    subProjects.addAll(projectDependencyNames())
    return ModuleRes.Java(name = project.named, files = files, projectDependencyNames = subProjects, projectDir = projectDir.absolutePath)
}

@Suppress("DEPRECATION")
fun Project.androidRes(): ModuleRes {
    val files = arrayListOf<FileRes>()
    val subProjects = arrayListOf<Named>()
    if (project.buildFile.exists()) {
        files.add(FileRes.Build(project.buildFile))
    }
    val android = extensions.findByName("android") as? BaseExtension
    val predicate: (AndroidSourceSet) -> Unit = {
        if (it.manifest.srcFile.exists()) {
            files.add(FileRes.Manifest(it.manifest.srcFile))
        }
        files.addAll(it.java.getSourceFiles().files.map(FileRes::Java))
        files.addAll(it.aidl.getSourceFiles().files.map(FileRes::Aidl))
        files.addAll(it.res.getSourceFiles().files.map(FileRes::Res))
        files.addAll(it.assets.getSourceFiles().files.map(FileRes::Assets))
        files.addAll(((it.kotlin as? DefaultAndroidSourceDirectorySet)?.getSourceFiles()?.files ?: emptyList()).map(FileRes::Kotlin))
    }
    android?.sourceSets?.findByName(MAIN)?.let(predicate)
    android?.sourceSets?.findByName(DEBUG)?.let(predicate)
    subProjects.addAll(projectDependencyNames())
    return ModuleRes.Android(name = project.named, files = files, projectDependencyNames = subProjects, projectDir = projectDir.absolutePath)
}

/**
 * record 任务的具体实现
 */
fun record(res: ModuleRes, respFile: File) {
    val json = res.toJson().toString()
    val file = File(respFile, "${res.named.artifactName}.json")
    file.parentFile.mkdirs()
    file.deleteOnExit()
    val buffer = Buffer()
    buffer.write(json.toByteArray(charset = Charsets.UTF_8))
    file.sink().use {
        it.write(buffer, buffer.size)
    }
    println(json)
}

/**
 * 补全字符串长度
 */
fun Any.complete(value: Int): String {
    val str = toString()
    return if (value <= str.length) {
        str.substring(0, value)
    } else {
        buildString {
            append(str)
            repeat(value - str.length) {
                append(" ")
            }
        }
    }
}