package com.mercer.kernel.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.extension.parent2T
import com.mercer.kernel.interfaces.HandledThrowable
import com.mercer.kernel.interfaces.HandledThrowableImpl
import com.mercer.kernel.interfaces.ThrowableInterceptor
import com.mercer.kernel.interfaces.interceptor.Interceptor
import com.mercer.kernel.interfaces.unify.OnRequireContext
import com.mercer.kernel.interfaces.unify.OnRequireLifecycle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   基础 Fragment
 */
abstract class AbsFragment : Fragment(),
    OnRequireContext,
    OnRequireLifecycle,
    HandledThrowable by HandledThrowableImpl() {

    ////////////////////////// Unify

    override fun requireLifecycleOwner(): LifecycleOwner = viewLifecycleOwner

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            handle(throwable)
        }
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope = scope

    ////////////////////////// Exception

    override val lastConsumer: ThrowableInterceptor by lazy {
        val handled = parent2T<HandledThrowable>()
        object : ThrowableInterceptor {
            override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
                val value = chain.input
                return if (handled != null) {
                    handled.handle(value)
                } else {
                    value
                }
            }
        }
    }

}