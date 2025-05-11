package com.mercer.magic.beans

import com.google.gson.Gson
import com.mercer.magic.dataFormat
import java.io.File

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   模块资源
 */
data class ModuleRes(
    val named: Named,
    val type: Type,
    val projectDir: String,
    val files: List<FileRes>,
    val projectDependencyNames: List<Named>,
    val lastModified: Long = files.maxOfOrNull { it.lastModified } ?: System.currentTimeMillis(),
    val lastModifiedTime: String = dataFormat.format(lastModified),
    val exists: Boolean = false
) {
    enum class Type {
        Java, Android
    }

    @Suppress("FunctionName")
    companion object {
        fun Java(name: Named, files: List<FileRes>, projectDependencyNames: List<Named>, projectDir: String): ModuleRes {
            return ModuleRes(named = name, type = Type.Java, projectDir = projectDir, files = files, projectDependencyNames = projectDependencyNames)
        }

        fun Android(name: Named, files: List<FileRes>, projectDependencyNames: List<Named>, projectDir: String): ModuleRes {
            return ModuleRes(named = name, type = Type.Android, projectDir = projectDir, files = files, projectDependencyNames = projectDependencyNames)
        }
    }

    fun toJson(): CharSequence {
        val files = files.map {
            it.copy(name = it.name.replace(arrayOf(projectDir, File.separator).joinToString(""), "").replace("\\", "/"))
        }
        val bean = copy(files = files, projectDir = projectDir.replace("\\", "/"))
        return Gson().toJson(bean)
    }

    override fun toString(): String {
        return buildString {
            append(named.artifactName)
            append(" : ")
            append(lastModifiedTime.chunked(8).joinToString(" "))
            append(" , ")
            append(files.size)
            append(" , ")
            append(exists)
        }
    }

}