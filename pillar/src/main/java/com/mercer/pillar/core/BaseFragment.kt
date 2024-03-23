package com.mercer.pillar.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.pillar.interfaces.support.OnThrowableHandle
import com.mercer.pillar.interfaces.unify.OnRequireLifecycle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope

/**
 * author:  mercer
 * date:    2024/3/23 00:20
 * desc:
 *   基础 fragment
 */
abstract class BaseFragment : Fragment(),
    OnRequireLifecycle,
    OnThrowableHandle {


    ///////////////////////////////////////////// Unify /////////////////////////////////////////////


    override fun requireLifecycleOwner(): LifecycleOwner {
        return viewLifecycleOwner
    }

    override fun requireLifecycle(): Lifecycle {
        return requireLifecycleOwner().lifecycle
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        handleThrowable(throwable)
    }

    private val scope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        // CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
        CoroutineScope(lifecycleScope.coroutineContext + handler + name)
    }


    override fun requireCoroutineScope(): CoroutineScope {
        return scope
    }

    ////////////////////////// Throwable //////////////////////////

    override fun handleThrowable(value: Throwable) {
        // 主要是转给 父节点 或者 当前的 Activity 中统一处理
        val parent = parentFragment
        parent as? OnThrowableHandle ?: requireActivity() as? OnThrowableHandle
        val handler = (parent ?: requireActivity()) as? OnThrowableHandle
        handler?.handleThrowable(value)
    }

}