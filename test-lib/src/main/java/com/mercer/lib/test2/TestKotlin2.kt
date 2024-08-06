package com.mercer.lib.test2

import com.mercer.annotate.http.Cache
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.annotate.http.Shared
import com.mercer.core.Mode
import com.mercer.core.OnShared
import com.mercer.lib.model.NetResult
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


@Decorator(SimpleCreator::class)
interface TestKotlin2 {

    @GET("/test6")
    fun func9(
        @Query("param1") v1: String,
        @Query("param2") v2: String,
    ): Flow<NetResult<String>>

    @Cache(SimpleCachePipeline::class)
    @POST("/test10")
    suspend fun func10(
        @JsonKey("v1") v1: String,
        @Query("v2") v2: Int,
        @JsonKey("v3") v3: Float,
    ): Flow<NetResult<String>>

    @Cache(SimpleCachePipeline2::class, mode = Mode.SELECT)
    @POST("/test10")
    fun func11(
        @JsonKey("v1") v1: String,
        @JsonKey("v2") v2: Int,
        @JsonKey("v3") v3: Double,
    ): Flow<NetResult<String>>


    @Shared(SimpleCachePipeline::class)
    interface User : OnShared<NetResult<String>> {

        @POST("/test12")
        suspend fun test12(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<String>>

    }

    @Shared(SimpleCachePipeline3::class)
    abstract class User2 : OnShared<NetResult<Int>> {

        @POST("/test12")
        abstract suspend fun test13(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<Int>>

    }

    companion object {
        fun xx(): User {
            return TestKotlin3Impl.user
        }
    }

}