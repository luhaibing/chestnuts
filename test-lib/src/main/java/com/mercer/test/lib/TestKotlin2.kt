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
interface TestKotlin2 {

    @GET("/test1")
  suspend  fun func1(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
    ): NetResult<String>


    @GET("/test2")
    suspend  fun func2(
        @Query("v1") v111: String,
        @Query("v2") v222: String,
    ): NetResult<String>


    @POST("/test3")
    suspend fun func3(
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): NetResult<String>

    @FormUrlEncoded
    @POST("/test4")
    suspend  fun func4(
        @Field("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): NetResult<String>

    @FormUrlEncoded
    @POST("/test5/{kkk}")
    suspend  fun func5(
        @Path("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): NetResult<String>

    companion object{
        operator fun invoke():TestKotlin2{
            return TestKotlin2Impl()
        }
    }

}