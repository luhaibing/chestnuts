package com.mercer.magic

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

/**
 * @author      Mercer
 * @Created     2025/05/12.
 * @Description:
 *   扩展
 */
inline fun <reified E> Collection<E>.onEach(crossinline action: E.() -> Unit) {
    forEach {
        it.action()
    }
}

inline fun Project.afterEvaluate(crossinline action: Project.() -> Unit) {
    afterEvaluate {
        action()
    }
}

inline fun Collection<Project>.afterEvaluate(crossinline action: Project.() -> Unit) {
    forEach { value ->
        value.afterEvaluate {
            it.action()
        }
    }
}


inline fun <reified E> ExtensionContainer.configure(crossinline action: E.() -> Unit) {
    configure(E::class.java) {
        it.action()
    }
}

fun PublishingExtension.maven(uri: URI) {
    repositories { handler ->
        handler.maven {
            it.url = uri
        }
    }
}

inline fun PublishingExtension.createPublication(name: String, crossinline action: MavenPublication.() -> Unit) {
    publications.create(name, MavenPublication::class.java) {
        it.action()
    }
}