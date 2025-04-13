package com.mercer.kernel.throwable.flow

import com.mercer.kernel.interfaces.interceptor.coroutines.Interceptor
import com.mercer.kernel.throwable.CombinedException
import com.mercer.kernel.throwable.PlainTextException
import com.mercer.kernel.throwable.get
import com.mercer.kernel.throwable.minusKey

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   明文类异常的消费者
 */
class PlainTextThrowableConsumer(
    private val predicate: (PlainTextException) -> Unit
) : ThrowableConsumer {

    override suspend fun intercept(chain: Interceptor.Chain<Throwable, Throwable?>): Throwable? {
        val input = chain.input
        /*
        val key = PlainTextException::class
        val find = input.getInstances(key)
        val result = if (find is CombinedException) {
            var throwable: Throwable? = input
            for (k in find.keys) {
                throwable = throwable?.minusKey(key)
                predicate(throwable as PlainTextException)
            }
            throwable
        } else if (find != null) {
            predicate(find as PlainTextException)
            input.minusKey(key)
        } else {
            input
        }
        */
        var throwable: Throwable? = input
        val keys = if (throwable is CombinedException) throwable.keys else arrayListOf(PlainTextException::class)
        for (k in keys) {
            val v = throwable?.get(k) ?: continue
            if (v is PlainTextException) {
                throwable = throwable.minusKey(k)
                predicate(v)
            }
        }
        throwable ?: return null
        return chain.proceed(throwable)
    }

}