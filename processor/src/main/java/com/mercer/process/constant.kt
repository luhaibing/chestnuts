package com.mercer.process

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.CacheKey
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.core.CacheKeys
import com.mercer.core.Converter
import com.mercer.core.Creator
import com.mercer.core.Path
import com.mercer.core.Provider
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentHashMap

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   常量
 */


/**
 * 变量相关的名字
 */
object Variable {
    // 真实接口类对象名字
    const val API_NAME = "api"

    // 接口类创建器对象的名字
    const val CREATOR_NAME = "onCreator"

    // 序列化器的名字
    const val CONVERTERS_NAME = "converters"
}

// core 相关的名字
object Core {
    // 处理器触发类
    val DECORATOR_CLASS_NAME = Decorator::class.asClassName()

    // 创建器
    val CREATOR_CLASS_NAME = Creator::class.asClassName()

    // 请求路径的描述类
    val PATH_CLASS_NAME = Path::class.asClassName()

    // 标记缓存 key 的注解
    val CACHE_KEY_CLASS_NAME = CacheKey::class.asClassName()

    // 存放缓存 key 的类
    val CACHE_KEYS_CLASS_NAME = CacheKeys::class.asClassName()

    val CORE_ANNOTATIONS = arrayOf(DECORATOR_CLASS_NAME, JsonKey::class.asClassName(), Append::class.asClassName(), CACHE_KEY_CLASS_NAME)

    val CONVERTER_FACTORY_NAME = Converter.Factory::class.asClassName()

    val PROVIDER_NAME = Provider::class.asClassName()
}

// 协程相关的名字
object Coroutines {
    // 协程 Flow
    val FLOW_CLASS_NAME = Flow::class.asClassName()

    // 协程 延迟获取结果
    val DEFERRED_CLASS_NAME = Deferred::class.asClassName()

    // 协程
    val COROUTINES = arrayOf(FLOW_CLASS_NAME, DEFERRED_CLASS_NAME)
    val COMPLETABLE_DEFERRED_CLASS_NAME = CompletableDeferred::class.asClassName()

    val FLOW_FUNCTION = MemberName("kotlinx.coroutines.flow", "flow")
    val RUN_BLOCKING_FLOW_FUNCTION = MemberName("kotlinx.coroutines", "runBlocking")
}

object Retrofit {
    const val RETROFIT2_HTTP_PACKAGE = "retrofit2.http"
}

object Kotlin {
    val STRING_NULLABLE = STRING.copy(nullable = true)

    val ANY_NULLABLE = ANY.copy(nullable = true)

    // 泛型 T
    val VARIABLE_NAME_T = TypeVariableName.invoke("T")

    // 泛型 *
    val VARIABLE_NAME_START = TypeVariableName.invoke("*")

    val TYPE_OF_NAME = MemberName("kotlin.reflect", "typeOf")

    val CONCURRENT_HASH_MAP_NAME = ConcurrentHashMap::class.asClassName()

    val CONVERTER_FACTORY_NAME = CONCURRENT_HASH_MAP_NAME.parameterizedBy(
        STRING, Core.CONVERTER_FACTORY_NAME.parameterizedBy(VARIABLE_NAME_START)
    )

}

// 换行
const val WRAP = "\r\n"