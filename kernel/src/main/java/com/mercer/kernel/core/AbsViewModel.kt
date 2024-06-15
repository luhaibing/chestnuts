package com.mercer.kernel.core

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mercer.kernel.interfaces.DerivedThrowable
import com.mercer.kernel.interfaces.DerivedThrowableImpl
import com.mercer.kernel.interfaces.HandledThrowable
import com.mercer.kernel.interfaces.HandledThrowableImpl
import com.mercer.kernel.interfaces.unify.OnRequireContext
import com.mercer.kernel.interfaces.unify.OnRequireCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * author:  Mercer
 * date:    2024/6/22
 * desc:
 *   基础 ViewModel
 */
class AbsViewModel(
    application: Application
) : AndroidViewModel(application),
    OnRequireCoroutineScope,
    OnRequireContext,
    HandledThrowable by HandledThrowableImpl(),
    DerivedThrowable by DerivedThrowableImpl() {


    ////////////////////////// Unify

    override fun requireContext(): Context {
        return getApplication()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            handle(throwable)?.let { dispatch(it) }
        }
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(viewModelScope.coroutineContext + handler + name)
    }

    override fun requireCoroutineScope(): CoroutineScope {
        return scope
    }

    ////////////////////////// throwable


}