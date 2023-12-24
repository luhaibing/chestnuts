package com.mercer.process

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * JDK 11 还是生效可调试的 17就不行了
 */
class DecoratorKaptProcessor : AbstractProcessor() {

    private lateinit var messages: Messager
    override fun init(environment: ProcessingEnvironment) {
        super.init(environment)
        messages = environment.messager
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return hashSetOf("*")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_17
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        messages.printMessage(Diagnostic.Kind.ERROR,"********* ${this.javaClass.canonicalName} *********")
        TODO("Not yet implemented")
    }

}
