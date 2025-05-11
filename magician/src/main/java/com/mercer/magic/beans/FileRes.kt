package com.mercer.magic.beans

import com.mercer.magic.dataFormat
import java.io.File

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   文件资源
 */

data class FileRes(
    val name: String,
    val fileType: Type,
    val lastModified: Long,
    val lastModifiedTime: String = dataFormat.format(lastModified),
) {

    enum class Type {
        Manifest,
        Build,
        Java,
        Kotlin,
        Aidl,
        Res,
        Assets,
        Resources,
    }

    @Suppress("FunctionName")
    companion object {
        fun Manifest(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Manifest, value.lastModified())
        }
        fun Build(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Build, value.lastModified())
        }
        fun Java(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Java, value.lastModified())
        }
        fun Kotlin(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Kotlin, value.lastModified())
        }
        fun Aidl(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Aidl, value.lastModified())
        }
        fun Res(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Res, value.lastModified())
        }
        fun Assets(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Assets, value.lastModified())
        }
        fun Resources(value: File): FileRes {
            return FileRes(value.absolutePath, Type.Resources, value.lastModified())
        }
    }

}