package com.mercer.lib.test2

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Cache
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.annotate.http.State
import com.mercer.core.OnState
import com.mercer.core.Strategy
//import com.mercer.core.Entry
import com.mercer.core.Type
import com.mercer.lib.model.NetResult
import com.mercer.lib.model.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


@Append(type = Type.QUERY, key = "q112", value = MyStringProvider::class)
@Append(type = Type.QUERY, key = "q111", value = MyStringProvider1::class)
@Append(type = Type.PART, key = "q113", value = MyStringProvider2::class)
@Append(type = Type.HEADER, key = "q114", value = MyStringProvider3::class)
@Decorator(SimpleCreator::class)
interface TestKotlin {

    // @Cache(pipeline = MyPipeline1::class)
    @Append(type = Type.QUERY, key = "q112", value = MyStringProvider4::class)
    @Append(type = Type.QUERY, key = "q111", value = MyStringProvider4::class)
    @GET("/test0")
    fun func1(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): Deferred<NetResult<String>>

    @Append(type = Type.QUERY, key = "q112", value = MyStringProvider4::class)
    @Append(type = Type.QUERY, key = "q111", value = MyStringProvider4::class)
    @GET("/test0")
    suspend fun func2(
        @QueryName(encoded = false) v111: String,
        @QueryName(encoded = true) v222: String,
        @JsonKey("kkk") kkk: String,
        @JsonKey("kkk2") kkk2: String = "3",
    ): NetResult<String>

    @Cache(pipeline = MyPipeline1::class)
    @GET("/test1")
    fun func3(
        @Query("param1") v1: String,
        @Query("param2") v2: String,
        @QueryName(encoded = false) v3: String,
        @QueryMap v4: Map<String, String>,
    ): Flow<NetResult<String>?>

    @Cache(pipeline = MyPipeline1::class, strategy = Strategy.SELECT)
    @POST("/test2")
    @FormUrlEncoded
    fun func4(
        @Field("param1") v1: String,
        @Field("param2") v2: String,
        @FieldMap v3: Map<String, String>,
    ): Flow<NetResult<String>>

    @POST("/test3")
    @Multipart
    fun func5(
        @Part("param1") v1: String,
        @Part("param2") v2: String,
        @Part("param3") v3: RequestBody,
        @Part v4: MultipartBody.Part,
        @Part v5: MultipartBody.Part,
        @PartMap v6: Map<String, String>,
    ): Flow<NetResult<String>>

    @POST("/test4")
    fun func6(
        @Body v1: Person,
        @Query("param2") v2: String
    ): Flow<NetResult<String>>

    @POST("/test5")
    @FormUrlEncoded
    fun func7(
        @Tag v1: String,
        @Query("param2") v2: Int,
        @Field("param3") v3: Int,
    ): Flow<NetResult<String>>

    @GET("/test6")
    @Headers("Cache-Control: max-age=640000")
    fun func8(): Flow<NetResult<String>>

    @GET("/test7")
    @Headers("X-Foo: Bar", "X-Ping: Pong")
    fun func9(): Flow<NetResult<String>>

    @GET("/test8")
    fun func10(
        @Header("Accept-Language") lang: String
    ): Flow<NetResult<String>>


    // @Append(type = Type.FIELD, key= "q117", value = MyStringProvider5::class)
    @GET("/test6")
    @Headers("Cache-Control: max-age=640000")
    fun func11(
        @Query("param1") v1: String,
        @Query("param2") v2: String,
        @QueryName(encoded = false) v3: String,
        @QueryMap v4: Map<String, String>,
    ): Flow<NetResult<String>>

    // @Append(type = Type.FIELD, key= "q117", value = MyStringProvider5::class)
    @POST("/test10")
    suspend fun func12(
        @JsonKey("v1") v1: String,
        @Query("v2") v2: Int,
        @JsonKey("v3") v3: Float,
    ): NetResult<String>

    @Cache(MyPipeline1::class)
    @POST("/test11")
    fun func13(
        @JsonKey("v1") v1: String,
        @JsonKey("v2") v2: Int,
        @JsonKey("v3") v3: Double,
    ): Flow<NetResult<String>>


    @State(MyPipeline4::class)
    abstract class User1 : OnState<NetResult<Int>?> {
        @POST("/test13")
        abstract suspend fun test14(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<Int>>
    }

    @State(MyPipeline2::class)
    interface User2 : OnState<NetResult<Int>> {

        @POST("/test14")
         fun test15(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<Int>>

        @POST("/test15")
         fun test16(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<Int>?>

        /*
        @POST("/test17")
        suspend fun test17(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<Float>>
        */

        override fun defaultValue(): NetResult<Int> {
            return NetResult(code = 4679, data = null, message = "viverra")
        }

    }

}