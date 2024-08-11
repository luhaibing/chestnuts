package com.mercer.test.lib

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.core.Type
import com.mercer.test.lib.model.Address
import com.mercer.test.lib.model.NetResult
import com.mercer.test.lib.model.Person
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryName

/**
 * author:  Mercer
 * date:    2024/08/10
 * desc:
 *   接口1
 */
@Append(type = Type.QUERY, key = "q118", value = MyProvider1::class)
@Decorator(SimpleCreator::class)
interface TestKotlin {

    @GET("/test1")
    fun func1(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
    ): Deferred<NetResult<Person>>

    @GET("/test2")
    fun func2(
        @Query("v1") v111: String,
        @Query("v2") v222: String,
    ): Flow<NetResult<Person>>

    @POST("/test3")
    suspend fun func3(
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): NetResult<Address>

    @FormUrlEncoded
    @POST("/test4")
    fun func4(
        @Field("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Call<NetResult<String>>

    @FormUrlEncoded
    @POST("/test5/{kkk}")
    fun func5(
        @Path("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Deferred<NetResult<String>>

    @Append(type = Type.QUERY, key = "q119", value = MyProvider2::class)
    @GET("func6/{user}/{user2}/repos")
    fun func6(
        @Path("user") v1: String,
        @Path("user2") v3: String,
        @Query("v2") v2: String,
        @Path("user3") v4: String,
    ): Deferred<NetResult<Person>>

    @FormUrlEncoded
    @POST("func7/{id}")
    fun func7(
        @Path("id") v1: String,
        @Query("v2") v2: String,
        @Field("v3") v3: String,
    ): Flow<NetResult<Address>>

    @FormUrlEncoded
    @POST("func8/{id}")
    fun func8(
        @Path("id") v1: String,
        @Query("v2") v2: String,
        @Field("v3") v3: String,
    ): Call<NetResult<Address>>

    companion object {
        operator fun invoke(): TestKotlin {
            return TestKotlinImpl()
        }
    }

}