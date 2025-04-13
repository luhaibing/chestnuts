package com.mercer.kernel.core

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mercer.kernel.interfaces.unity.OnRequireActivityUnity
import com.mercer.kernel.interfaces.unity.OnRequireContextUnity
import com.mercer.kernel.interfaces.unity.OnRequireCoroutineScopeUnity
import com.mercer.kernel.interfaces.unity.OnRequireLifecycleOwnerUnity
import com.mercer.kernel.throwable.PlainTextException
import com.mercer.kernel.throwable.event.LogoutException
import com.mercer.kernel.throwable.flow.BackPressedExceptionConsumer
import com.mercer.kernel.throwable.flow.LogoutExceptionConsumer
import com.mercer.kernel.throwable.flow.NavigateExceptionConsumer
import com.mercer.kernel.throwable.flow.OnThrowableHandler
import com.mercer.kernel.throwable.flow.PlainTextThrowableConsumer
import com.mercer.kernel.throwable.flow.RouteToPathExceptionConsumer
import com.mercer.kernel.throwable.flow.ThrowableConsumer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   cc
 */
abstract class AbsActivity : AppCompatActivity(),
    OnRequireContextUnity,
    OnRequireActivityUnity,
    OnRequireCoroutineScopeUnity,
    OnRequireLifecycleOwnerUnity,
    OnThrowableHandler {

    /////////////////////////////////////////////////// require

    override fun requireActivity(): AppCompatActivity {
        return this
    }

    override val coroutineScope: CoroutineScope by lazy {
        val name = CoroutineName("${javaClass.simpleName}-CoroutineScope")
        CoroutineScope(requireLifecycleOwner().lifecycleScope.coroutineContext + handler + name)
    }

    override fun requireLifecycleOwner(): LifecycleOwner {
        return this
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

    override fun initializeThrowableHandler() {
        throwableConsumers.add(BackPressedExceptionConsumer {
            onBackPressedDispatcher.onBackPressed()
            true
        })
        throwableConsumers.add(LogoutExceptionConsumer(::toLogin))
        throwableConsumers.add(RouteToPathExceptionConsumer {
            routeToPath(it.path)
        })
        throwableConsumers.add(NavigateExceptionConsumer {
            navigateToRes(it.navigationRes)
        })
        throwableConsumers.add(PlainTextThrowableConsumer(::plainTextExceptionConsume))
    }

    open fun navigateToRes(res: Int): Boolean {
        return false
    }

    open fun toLogin(value: LogoutException): Boolean {
        return false
    }

    open fun routeToPath(value: String): Boolean {
        return false
    }

    /**
     * 明文异常
     */
    open fun plainTextExceptionConsume(value: PlainTextException) {
        Toast.makeText(this, value.message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeThrowableHandler()
    }

}