package com.mercer.annotate.http

import com.mercer.core.DefaultPersistenceDispatcher
import com.mercer.core.OnPersistence
import com.mercer.core.PersistenceDispatcher
import kotlin.reflect.KClass

/**
 * @author :Mercer
 * @Created on 2024/11/26.
 * @Description:
 *   数据持久化
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Persistence(
    // 具体的持久化逻辑
    val value: KClass<out OnPersistence>,
    // 持久化数据的调度器
    val dispatcher: KClass<out PersistenceDispatcher> = DefaultPersistenceDispatcher::class
)