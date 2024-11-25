package com.mercer.kernel.core

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.interfaces.require.OnRequireActivity
import com.mercer.kernel.interfaces.require.OnRequireContext
import com.mercer.kernel.interfaces.require.OnRequireCoroutineScope
import com.mercer.kernel.interfaces.require.OnRequireLifecycleOwner
import com.mercer.kernel.interfaces.throwable.OnThrowableHandle
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
abstract class AbsActivity : AppCompatActivity(),
    OnRequireContext, OnRequireActivity,
    OnRequireCoroutineScope, OnRequireLifecycleOwner,
    ThrowableDispatcher, OnThrowableHandle {

    /////////////////////////////////////////////////// require

    override val coroutineScope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireActivity(): AppCompatActivity {
        return this
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return this
    }

    /////////////////////////////////////////////////// throwable

    override val throwableConsumer: MutableList<ThrowableConsumer> by lazy {
        arrayListOf()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            dispatch(throwable)?.printStackTrace()
        }
    }

    override suspend fun dispatch(value: Throwable): Throwable? {
        return handle(value) ?: super.dispatch(value)
    }


    ///////////////////////////////////////////////////

}