package com.mercer.kernel.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.interfaces.OnLifecycleOwnerSupport
import com.mercer.kernel.interfaces.OnLifecycleOwnerSupportImpl
import com.mercer.kernel.interfaces.unity.OnRequireCoroutineScopeUnity
import com.mercer.kernel.interfaces.unity.OnRequireLifecycleOwnerUnity
import com.mercer.kernel.throwable.flow.OnThrowableForward
import com.mercer.kernel.throwable.flow.OnThrowableHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   12
 */
abstract class AbsViewModel : ViewModel(),
    OnLifecycleOwnerSupport by OnLifecycleOwnerSupportImpl(),
    OnRequireCoroutineScopeUnity,
    OnRequireLifecycleOwnerUnity,
    OnThrowableHandler, OnThrowableForward {

    /////////////////////////////////////////////////// 生命周期

    override fun onCleared() {
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onCleared()
    }

    /////////////////////////////////////////////////// require

    override val coroutineScope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return this
    }

    /////////////////////////////////////////////////// 异常处理

    // TODO: 异常处理
    private val handler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        requireCoroutineScope().launch {
            handle(throwable)
        }
    }

}