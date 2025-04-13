package com.mercer.kernel.util

import com.mercer.kernel.throwable.CancelException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.time.Duration

/**
 * @author :Mercer
 * @Created on 2025/04/12.
 * @Description:
 *   重试
 */

/**
 * 重试
 * @param tries         重试次数
 * @param interval      间隔
 * @param proceed       根据异常是否继续重试
 * @param block         执行回调
 */
@Throws(CancellationException::class)
suspend fun <T> retry(
    tries: Int,
    interval: (RetryScope.() -> Duration?)? = null,
    proceed: (RetryScope.(Throwable) -> Boolean)? = null,
    block: suspend () -> T
): Result<T> {
    require(tries > 0)
    with(RetryScopeImpl()) {
        while (true) {
            currentCoroutineContext().ensureActive()
            val result = runCatching {
                block()
            }
            currentCoroutineContext().ensureActive()
            if (result.isSuccess) {
                return result
            }
            val throwable = checkNotNull(result.exceptionOrNull())
            val forward = proceed?.invoke(this, throwable) ?: true
            if (forward.not()) {
                return result
            }
            if (times >= tries) {
                return Result.failure(TriesFailureException(throwable))
            }
            val duration = interval?.invoke(this)
            if (duration != null) {
                delay(duration)
            }
            increase()
        }
    }
}

/**
 * 达到最大执行次数时抛出的异常
 */
class TriesFailureException(cause: Throwable) : CancelException(cause)

interface RetryScope {
    /**
     * 当前重试次数
     */
    val times: Int
}

private class RetryScopeImpl : RetryScope {

    private var _times = 0

    override val times: Int
        get() = _times

    fun increase() {
        _times++
    }

}