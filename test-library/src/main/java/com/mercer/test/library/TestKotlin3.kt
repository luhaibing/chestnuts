package com.mercer.test.library

import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.test.library.model.NetResult
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryName
import retrofit2.http.Url

/**
 * author:  Mercer
 * date:    2024/9/3
 * desc:
 *   接口1
 */
@Decorator(SimpleCreator::class)
interface TestKotlin3 {

    @GET("/test1")
    fun func1(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
    ): Flow<NetResult<String>>


    @GET("/test2")
    fun func2(
        @Query("v1") v111: String,
        @Query("v2") v222: String,
    ): Flow<NetResult<String>>


    @POST("/test3")
    fun func3(
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): Flow<NetResult<String>>

    @FormUrlEncoded
    @POST("/test4")
    fun func4(
        @Field("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Flow<NetResult<String>>

    @FormUrlEncoded
    @POST("/test5/{kkk}")
    fun func5(
        @Path("kkk") kkk: String,
        @Field("kkk2") kkk2: String = "3",
    ): Flow<NetResult<String>>

    companion object{
        operator fun invoke():TestKotlin3{
            return TestKotlin3Impl()
        }
    }

    /**
     * 上传
     */
    @PUT
    fun uploadFile(
        @Url uri: String,
        @Body file: RequestBody
    ): Call<ResponseBody>

}