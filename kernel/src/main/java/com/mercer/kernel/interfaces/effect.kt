package com.mercer.kernel.interfaces

import com.mercer.kernel.core.intent.Effect
import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.interfaces.interceptor.LoopChain

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   影响
 */
interface EffectInterceptor : Interceptor<Effect, Effect>

interface HandledEffect {

    /**
     * 第一个消费者
     */
    val firstConsumer: EffectInterceptor

    /**
     * 中间消费者
     */
    val throwableInterceptors: List<EffectInterceptor>

    /**
     * 最后的消费者
     */
    val lastConsumer: EffectInterceptor

    suspend fun handle(value: Effect): Effect? {
        val result = LoopChain.start(value, arrayListOf<EffectInterceptor>().apply {
            add(firstConsumer)
            addAll(throwableInterceptors)
            add(lastConsumer)
        })
        return result
    }

}

class HandledEffectImpl : HandledEffect {

    override val firstConsumer: EffectInterceptor
        get() = TODO("Not yet implemented")

    override val throwableInterceptors: List<EffectInterceptor>
        get() = TODO("Not yet implemented")

    override val lastConsumer: EffectInterceptor
        get() = TODO("Not yet implemented")

}