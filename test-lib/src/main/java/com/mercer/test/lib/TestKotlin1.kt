package com.mercer.test.lib

import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.test.lib.model.NetResult
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryName

/**
 * author:  Mercer
 * date:    2024/9/3
 * desc:
 *   接口1
 */
@Decorator(SimpleCreator::class)
interface TestKotlin1 {

    @GET("/test1")
    fun func1(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
    ): Deferred<NetResult<String>>


    @GET("/test2")
    fun func2(
        @Query("v1") v111: String,
        @Query("v2") v222: String,
    ): Deferred<NetResult<String>>


    @POST("/test3")
    fun func3(
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): Deferred<NetResult<String>>

    @FormUrlEncoded
    @POST("/test4")
    fun func4(
        @Field("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Deferred<NetResult<String>>

    @FormUrlEncoded
    @POST("/test5/{kkk}")
    fun func5(
        @Path("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Deferred<NetResult<String>>

    companion object{
        operator fun invoke():TestKotlin1{
            return TestKotlin1Impl()
        }
    }

}