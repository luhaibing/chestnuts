package com.mercer.kernel.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.interfaces.require.OnRequireCoroutineScope
import com.mercer.kernel.interfaces.require.OnRequireLifecycleOwner
import com.mercer.kernel.interfaces.throwable.OnThrowableHandle
import com.mercer.kernel.interfaces.throwable.OnThrowableForward
import com.mercer.kernel.interfaces.throwable.ThrowableConsumer
import com.mercer.kernel.interfaces.throwable.ThrowableDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   超类
 */
abstract class AbsViewModel : ViewModel(),
    OnRequireCoroutineScope, OnRequireLifecycleOwner,
    ThrowableDispatcher, OnThrowableHandle, OnThrowableForward,
    LifecycleOwner {

    /////////////////////////////////////////////////// require

    override val coroutineScope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return this
    }

    /////////////////////////////////////////////////// throwable

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            dispatch(throwable)
        }
    }

    override val throwableConsumer: MutableList<ThrowableConsumer> by lazy {
        arrayListOf()
    }

    override suspend fun dispatch(value: Throwable): Throwable? {
        return handle(value) ?: super.dispatch(value)
    }

    override suspend fun unhandledException(value: Throwable): Throwable? {
        channel.send(value)
        return null
    }

    /////////////////////////////////////////////////// lifecycle

    final override val lifecycle: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onCleared() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onCleared()
    }

    ///////////////////////////////////////////////////

}