package com.mercer.core

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2023/12/24
 * desc:
 *   注解处理器的触发点的参数,用于创建 retrofit 操作的接口对象
 */
interface Creator {

    fun <T : Any> create(service: KClass<T>): T

    // 生成的代码不再使用该方法转Flow
    @Deprecated("The generated code no longer uses this method to Flow.")
    fun <T> suspend2flow(block: suspend () -> T): Flow<T> = TODO()

    // 生成的代码不再使用该方法转Deferred
    @Deprecated("The generated code no longer uses this method to Deferred.")
    fun <T> suspend2deferred(block: suspend () -> T): Deferred<T> = TODO()

    // fun any2str(value: Any?): String?

    // val scope: CoroutineScope

}