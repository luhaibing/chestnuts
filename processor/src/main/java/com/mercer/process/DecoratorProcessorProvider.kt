package com.mercer.process

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * author:  mercer
 * date:    2024/2/15 00:44
 * desc:
 *   静态代理的生成逻辑的向外暴露
 */
class DecoratorProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DecoratorSymbolProcessor(environment)
    }
}