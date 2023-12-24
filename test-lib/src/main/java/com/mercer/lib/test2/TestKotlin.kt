package com.mercer.lib.test2

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Decorator
import com.mercer.core.Entry
import com.mercer.core.Type
import com.mercer.test.lib.*
import com.mercer.test.lib.model.NetResult
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*


@Append(value = Type.FIELD, entry = Entry(name = "q112", value = MyStringProvider::class))
@Append(value = Type.QUERY, entry = Entry(name = "q111", value = MyStringProvider1::class))
@Append(value = Type.PART, entry = Entry(name = "q113", value = MyStringProvider2::class))
@Append(value = Type.HEADER, entry = Entry(name = "q114", value = MyStringProvider3::class))
@Append(value = Type.BODY, entry = Entry(name = "", value = MyIntProvider::class))
@Decorator(SimpleCreator::class)
interface TestKotlin {

    @Append(value = Type.QUERY, entry = Entry(name = "q117", value = MyStringProvider4::class))
    @GET("/test0")
    fun func0(
        @QueryName(encoded = false) v1: String,
        @QueryName(encoded = true) v2: String,
    ): Flow<NetResult<String>>

//    @GET("/test1")
//    fun func1(
//        @Query("param1") v1: String,
//        @Query("param2") v2: String,
//        @QueryName(encoded = false) v3: String,
//        @QueryMap v4: Map<String, String>,
//    ): Flow<NetResult<String>>
//
//    @POST("/test2")
//    @FormUrlEncoded
//    fun func2(
//        @Field("param1") v1: String,
//        @Field("param2") v2: String,
//        @FieldMap v3: Map<String, String>,
//    ): Flow<NetResult<String>>
//
//    @POST("/test3")
//    @Multipart
//    fun func3(
//        @Part("param1") v1: String,
//        @Part("param2") v2: String,
//        @Part("param3") v3: RequestBody,
//        @Part v4: MultipartBody.Part,
//        @Part v5: MultipartBody.Part,
//        @PartMap v6: Map<String, String>,
//    ): Flow<NetResult<String>>
//
//    @POST("/test4")
//    fun func4(
//        @Body v1: Person,
//        @Query("param2") v2: String
//    ): Flow<NetResult<String>>
//
//    @POST("/test5")
//    @FormUrlEncoded
//    fun func5(
//        @Tag v1: String,
//        @Query("param2") v2: Int,
//        @Field("param3") v3: Int,
//    ): Flow<NetResult<String>>
//
//    @GET("/test6")
//    @Headers("Cache-Control: max-age=640000")
//    fun func6(): Flow<NetResult<String>>
//
//    @GET("/test7")
//    @Headers("X-Foo: Bar", "X-Ping: Pong")
//    fun func7(): Flow<NetResult<String>>
//
//    @GET("/test8")
//    fun func8(
//        @Header("Accept-Language") lang: String
//    ): Flow<NetResult<String>>
//
//
//    @Append(value = Type.FIELD, entry = Entry(name = "q117", value = MyStringProvider5::class))
//    @GET("/test6")
//    @Headers("Cache-Control: max-age=640000")
//    fun func9(
//        @Query("param1") v1: String,
//        @Query("param2") v2: String,
//        @QueryName(encoded = false) v3: String,
//        @QueryMap v4: Map<String, String>,
//    ): Flow<NetResult<String>>

}