package com.mercer.magic

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   常量
 */
@Suppress("SimpleDateFormat")
val dataFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmmSSS")

const val MAIN: String = "main"
const val DEBUG: String = "debug"

val ASSEMBLE_TASK_REGEX: Regex = ":?[a-zA-Z0-9]+:assemble(.*)(debug|trial|release)".toRegex(RegexOption.IGNORE_CASE)
val PUBLISH_TASK_REGEX: Regex = "publish((.*Publications?ToMaven(Repository|Local))|ToMavenLocal)?".toRegex(RegexOption.IGNORE_CASE)
val BUNDLE_TASK_REGEX: Regex = "bundleLib(.*)To(jar|dir)debug".toRegex(RegexOption.IGNORE_CASE)

val CONFIGURATION_REGEX = "^.*(implementation|api|runtimeOnly|compileOnly)$".toRegex(RegexOption.IGNORE_CASE)