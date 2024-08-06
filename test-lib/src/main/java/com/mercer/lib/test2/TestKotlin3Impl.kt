package com.mercer.lib.test2

import com.google.gson.reflect.TypeToken
import com.mercer.core.CachePipeline
import com.mercer.core.Creator
import com.mercer.core.Mode
import com.mercer.core.Path
import com.mercer.lib.model.NetResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TestKotlin3Impl : TestKotlin2 {

    private val onCreator: Creator by lazy { SimpleCreator() }
    private val api: TestKotlin2Service by lazy { onCreator.create(TestKotlin2Service::class) }

    private constructor()

    override fun func9(v1: String, v2: String): Flow<NetResult<String>> =
        onCreator.suspend2flow {
            api.func9_CAD92420023139198D128EA3107AFCC6(
                v1 = v1,
                v2 = v2,
            )
        }

    private val pipeline by lazy {
        SimpleCachePipeline()
    }

    override suspend fun func10(
        v1: String,
        v2: Int,
        v3: Float,
    ): Flow<NetResult<String>> {
        val v4 = hashMapOf<String, Any?>()
        v4["v1"] = v1
        v4["v3"] = v3
//        return flow {
//            /*
//            val fromCache = onCreator.suspend2deferred {
//                val result = runCatching {
//                    pipeline.read(Path.POST("/test10"))
//                }
//                while (result.getOrNull() == null) {
//                    delay(1.seconds)
//                }
//                result
//            }
//            val fromNetwork = onCreator.suspend2deferred {
//                runCatching {
//                    api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
//                }
//            }
//            val value: Result<NetResult<String>> = select {
//                fromCache.onAwait {
//                    Result(it.getOrNull()!!, Cache)
//                }
//                fromNetwork.onAwait {
//                    Result(it.getOrNull()!!, Network)
//                }
//            }
//            fromCache.cancel()
//            fromNetwork.cancel()
//            emit(value)
//            if (value.source != Network) {
//                emit(Result(api.func11_1C66446740E011048286703B88EAF94D(v1 = v4), Network))
//            }
//            */
//            SelectedCacheStrategy.process(
//                collector = this,
//                path = Path.POST("/test10"),
//                onCache = {
//                    null
//                },
//                onNetwork = suspend {
//                    api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
//                }
//            )
//        }.onEach {
//            if (it.source == Network) {
//                pipeline.write(Path.POST("/test10"), it.value)
//            }
//        }.map {
//            it.value
//        }
        val path = Path.POST("/test10")
        return Mode.SELECT(path, pipeline) {
            api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
        }
    }

    override fun func11(
        v1: String,
        v2: Int,
        v3: Double,
    ): Flow<NetResult<String>> {
        val v4 = hashMapOf<String, Any?>()
        v4["v1"] = v1
        v4["v2"] = v2
        v4["v3"] = v3
        val path = Path.POST("/test11")
        val type = object : TypeToken<NetResult<String>>() {}.type
//        return flow {
//            /*
//            val cache = pipeline.read(Path.POST("/test10"))
//            if (cache != null) {
//                emit(Result(cache, Cache))
//            }
//            val resp = api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
//            emit(Result(resp, Network))
//            */
//            DefaultCacheStrategy.process(
//                collector = this,
//                path = path,
//                onCache = {
//                    pipeline.read(path, type)
//                },
//                onNetwork = {
//                    api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
//                })
//        }.onEach {
//            if (it.source == Network) {
//                pipeline.write(path, it.value as? NetResult<String>)
//            }
//        }.map {
//            it.value
//        }
        return Mode.DEFAULT(path, pipeline) {
            api.func11_1C66446740E011048286703B88EAF94D(v1 = v4)
        }
    }

//    /**
//     *
//     */
//    private suspend fun <T> FlowCollector<Result<T>>.useCache2(
//        path: Path,
//        fromCache: suspend (Path) -> T,
//        fromNetwork: suspend () -> T
//    ) {
//        /*
//        val value = supervisorScope {
//            val fromCacheDeferred: Deferred<T> = async {
//                val cache = fromCache(path)
//                while (cache == null) {
//                    delay(1.seconds)
//                }
//                cache
//            }
//            val fromNetworkDeferred: Deferred<T> = async {
//                fromNetwork()
//            }
//            val result = select {
//                fromCacheDeferred.onAwait {
//                     Result(it, Cache)
//                }
//                fromNetworkDeferred.onAwait {
//                     Result(it, Network)
//                }
//            }
//            fromCacheDeferred.cancel()
//            fromNetworkDeferred.cancel()
//            result
//        }
//        emit(value)
//        if (value.source != Network) {
//            emit( Result(fromNetwork(), Network))
//        }
//        */
//
//        /*
//        supervisorScope {
//            val fromCacheDeferred: Deferred<T> = async {
//                val cache = fromCache(path)
//                while (cache == null) {
//                    delay(1.seconds)
//                }
//                cache
//            }
//            val fromNetworkDeferred: Deferred<T> = async {
//                fromNetwork()
//            }
//            val result = select {
//                fromCacheDeferred.onAwait {
//                     Result(it, Cache)
//                }
//                fromNetworkDeferred.onAwait {
//                     Result(it, Network)
//                }
//            }
//            emit(result)
//            if (result.source != Network) {
//                val resp = fromCacheDeferred.await()
//                emit( Result(resp, Network))
//            }
//            fromCacheDeferred.cancel()
//            fromNetworkDeferred.cancel()
//        }
//        */
//
////        DefaultCacheStrategy.process(this, path, fromCache, fromNetwork)
////        SelectedCacheStrategy.process(this, path, fromCache, fromNetwork)
//        TODO()
//    }
//
//    /**
//     * 默认的使用的缓存的策略
//     */
//    private suspend fun <T> FlowCollector<Result<T>>.useCache1(
//        path: Path,
//        fromCache: suspend (Path) -> T,
//        fromNetwork: suspend () -> T
//    ) {
//        val cache = kotlin.runCatching { fromCache(path) }.getOrNull()
//        if (cache != null) {
//            emit(Result(cache, Cache))
//        }
//        val resp = fromNetwork()
//        emit(Result(resp, Network))
//    }

    fun func12(
        v1: Int,
        v2: Float,
        v3: Int,
    ): Flow<NetResult<String>> {
        val hashMapOf = hashMapOf<String, Any>()
        hashMapOf["v2"] = v2
        hashMapOf["v3"] = v3
        return onCreator.suspend2flow {
            api.func11_1C66446740E011048286703B88EAF94D(v1 = v1)
        }
    }

    private inner class UserImpl3 :
        TestKotlin2.User /*, AbsSharedData<NetResult<String>>("TestKotlin2.User")*/ {

        val currentAsFlow = MutableStateFlow<NetResult<String>?>(null)
        override suspend fun test12(
            age: Int,
            height: Float,
            name: String
        ): Flow<NetResult<String>> {
            TODO("Not yet implemented")
        }

        override val pipeline: CachePipeline<NetResult<String>> = this@TestKotlin3Impl.pipeline

        override val currentFlow: MutableStateFlow<NetResult<String>?> = MutableStateFlow(null)

        private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        protected val scope: CoroutineScope = CoroutineScope(
            Dispatchers.IO + exceptionHandler + CoroutineName("TestKotlin2.User")
        )

        init {
            scope.launch {
                val value = pipeline.read(Path.POST("/test11"))
                currentAsFlow.emit(value)
            }
        }

         suspend fun test12(v1: Int, v2: Float, v3: Int): Flow<NetResult<String>> {
            return TestKotlin3Impl().func12(v1, v2, v3).onEach {
                pipeline.write(Path.POST("/test11"), it)
            }
        }

    }

    companion object {
        operator fun invoke(): TestKotlin3Impl = Holder.INSTANCE
        val user: TestKotlin2.User by lazy {
            Holder.INSTANCE.UserImpl3()
        }
    }

    private object Holder {
        val INSTANCE: TestKotlin3Impl = TestKotlin3Impl()
    }


    public class UserImpl : TestKotlin2.User {


        override suspend fun test12(
            age: Int,
            height: Float,
            name: String
        ): Flow<NetResult<String>> {
            TODO("Not yet implemented")
        }

        override val pipeline: CachePipeline<NetResult<String>> = TODO()
        override val currentFlow: MutableStateFlow<NetResult<String>?> = MutableStateFlow(null)

    }

    public inner class User2Impl : TestKotlin2.User2() {

        override suspend fun test13(
            age: Int,
            height: Float,
            name: String,
        ): Flow<NetResult<String>> {
            return User2_test13(age = age, height = height, name = name).onEach {
                pipeline.write(Path.POST("/test12"), it)
            }
        }


        override val pipeline: CachePipeline<NetResult<String>> = TODO()
        override val currentFlow: MutableStateFlow<NetResult<String>?> = MutableStateFlow(null)

    }

    fun User2_test13(
        age: Int,
        height: Float,
        name: String,
    ): Flow<NetResult<String>> {
        TODO()
    }

}

