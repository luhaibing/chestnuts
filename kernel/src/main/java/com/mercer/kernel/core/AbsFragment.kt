package com.mercer.kernel.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.extension.parentAs
import com.mercer.kernel.interfaces.unity.OnRequireContextUnity
import com.mercer.kernel.interfaces.unity.OnRequireCoroutineScopeUnity
import com.mercer.kernel.interfaces.unity.OnRequireFragmentManagerUnity
import com.mercer.kernel.interfaces.unity.OnRequireLifecycleOwnerUnity
import com.mercer.kernel.throwable.flow.NavigateExceptionConsumer
import com.mercer.kernel.throwable.flow.OnThrowableForward
import com.mercer.kernel.throwable.flow.OnThrowableHandler
import com.mercer.kernel.throwable.flow.ProceedFailedException
import com.mercer.kernel.throwable.flow.ThrowableConsumer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   1
 */
abstract class AbsFragment : Fragment(),
    OnRequireContextUnity,
    OnRequireCoroutineScopeUnity,
    OnRequireLifecycleOwnerUnity,
    OnRequireFragmentManagerUnity,
    OnThrowableHandler, OnThrowableForward {

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

    /////////////////////////////////////////////////// 异常处理

    private val handler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        requireCoroutineScope().launch {
            handle(throwable)
        }
    }

    override val throwableConsumers: MutableList<ThrowableConsumer> by lazy {
        arrayListOf()
    }

    override val channel: Channel<Throwable> by lazy {
        Channel(capacity = Channel.BUFFERED)
    }

    /**
     * 初始化异常消费者的列表
     */
    override fun initializeThrowableHandler() {
        throwableConsumers.add(NavigateExceptionConsumer {
            navigateToRes(it.navigationRes)
        })
        channel.receiveAsFlow().consume { handle(it) }
        derivedOf().consume {
            val handler = parentAs<OnThrowableHandler>()
            if (handler != null) {
                handler.handle(it)
            } else {
                handle(ProceedFailedException(null, it))
            }
        }
    }

    override suspend fun unhandled(value: Throwable): Throwable? {
        if (value is ProceedFailedException) {
            value.printStackTrace()
        } else {
            channel.send(value)
        }
        return null
    }

    open fun navigateToRes(res: Int): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeThrowableHandler()
    }

}