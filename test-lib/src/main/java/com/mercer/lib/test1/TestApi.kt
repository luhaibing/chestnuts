package com.mercer.lib.test1

import com.mercer.lib.model.NetResult
import com.mercer.lib.model.Person
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart

import retrofit2.http.Query
import retrofit2.http.QueryName
import retrofit2.http.QueryMap

import retrofit2.http.Field
import retrofit2.http.FieldMap

import retrofit2.http.Part
import retrofit2.http.PartMap

import retrofit2.http.Body
import retrofit2.http.Url
import retrofit2.http.Headers
import retrofit2.http.Header
import retrofit2.http.Tag


interface TestApi {

    @GET("/test0")
    suspend fun func0(
        @QueryName(encoded = false) v1: String,
        @QueryName(encoded = true) v2: String,
    ): NetResult<String>

    @GET("/test1")
    suspend fun func1(
        @Query("param1") v1: String,
        @Query("param2") v2: String,
        @QueryName(encoded = false) v3: String,
        @QueryMap v4: Map<String, String>,
    ): NetResult<String>

    @POST("/test2")
    @FormUrlEncoded
    suspend fun func2(
        @Field("param1") v1: String,
        @Field("param2") v2: String,
        @FieldMap v3: Map<String, String>,
    ): NetResult<String>

    @POST("/test3")
    @Multipart
    suspend fun func3(
        @Part("param1") v1: String,
        @Part("param2") v2: String,
        @Part("param3") v3: RequestBody,
        @Part v4: MultipartBody.Part,
        @Part v5: MultipartBody.Part,
        @PartMap v6: Map<String, String>,
    ): NetResult<String>

    @POST("/test4")
    suspend fun func4(
        @Body v1: Person,
        @Query("param2") v2: String
    ): NetResult<String>

    @POST("/test5")
    @FormUrlEncoded
    suspend fun func5(
        @Tag v1: String,
        @Query("param2") v2: Int,
        @Field("param3") v3: Int,
    ): NetResult<String>

    @GET("/test6")
    @Headers("Cache-Control: max-age=640000")
    suspend fun func6(): NetResult<String>

    @GET("/test7")
    @Headers("X-Foo: Bar", "X-Ping: Pong")
    suspend fun func7(): NetResult<String>

    @GET("/test8")
    @Headers("X-Ping: Pong")
    suspend fun func8(
        @Header("Accept-Language") lang: String
    ): NetResult<String>

    @GET
    suspend fun func9(
        @Url url: String,
        @Tag tag: String,
    ): NetResult<String>

}