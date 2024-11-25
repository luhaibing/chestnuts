package com.mercer.kernel.interfaces.effect

import com.mercer.kernel.core.intent.Effect
import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.interfaces.interceptor.LoopChain

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   影响
 */
typealias EffectConsumer = Interceptor<Effect?, Effect?>

class EffectLoopChain private constructor(
    position: Int = 0,
    values: List<EffectConsumer>
) : LoopChain<Effect?, Effect?>(position, values) {

    override suspend fun proceed(value: Effect?): Effect? {
        if (value == null) {
            return null
        }
        return super.proceed(value)
    }

    override fun next(): LoopChain<Effect?, Effect?> {
        return EffectLoopChain(current + 1, interceptors).also {
            it.input = input
        }
    }

    companion object {

        operator fun invoke(vararg values: EffectConsumer): EffectLoopChain {
            return EffectLoopChain(0, values.asList())
        }

        operator fun invoke(interceptors: List<EffectConsumer>): EffectLoopChain {
            return invoke(*interceptors.toTypedArray())
        }

    }

}