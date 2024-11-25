package com.mercer.kernel.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.extension.parent2T
import com.mercer.kernel.interfaces.require.OnRequireCoroutineScope
import com.mercer.kernel.interfaces.require.OnRequireFragmentManager
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
abstract class AbsFragment : Fragment(),
    OnRequireCoroutineScope, OnRequireLifecycleOwner,
    OnRequireFragmentManager,
    ThrowableDispatcher, OnThrowableHandle {

    /////////////////////////////////////////////////// require

    override val coroutineScope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return viewLifecycleOwner
    }

    override fun getSupportFragmentManager(): FragmentManager {
        return childFragmentManager
    }

    /////////////////////////////////////////////////// throwable

    override val throwableConsumer: MutableList<ThrowableConsumer> by lazy {
        arrayListOf()
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        requireCoroutineScope().launch {
            dispatch(throwable)
        }
    }

    override suspend fun dispatch(value: Throwable): Throwable? {
        return handle(value) ?: super.dispatch(value)
    }

    override suspend fun unhandledException(value: Throwable): Throwable? {
        return parent2T<ThrowableDispatcher>()?.dispatch(value) ?: super.unhandledException(value)
    }

    ///////////////////////////////////////////////////

    override fun onResume() {
        super.onResume()
        requireContext()
        requireActivity()
    }

}