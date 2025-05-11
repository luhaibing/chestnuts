package com.mercer.magic.beans

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   名字
 */
data class Named(
    val name: String,
    val path: String,
    val artifactName: String = path.split(":").filter(String::isNotBlank).joinToString("_"),
)