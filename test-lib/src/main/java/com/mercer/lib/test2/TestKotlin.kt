package com.mercer.lib.test2

import com.mercer.annotate.http.Append
import com.mercer.annotate.http.Cache
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.annotate.http.Serialization
import com.mercer.annotate.http.State
import com.mercer.core.OnState
import com.mercer.core.Strategy
//import com.mercer.core.Entry
import com.mercer.core.Type
import com.mercer.lib.model.Address
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
@Serialization(MyGsonSerializer::class)
interface TestKotlin {

    @Cache(pipeline = MyPipeline1::class)
    @GET("/test1")
    fun func3(
        @Query("param1") v1: String,
        @Query("param2") v2: String,
        @QueryName(encoded = false) v3: String,
        @QueryMap v4: Map<String, String>,
    ): Flow<NetResult<String>?>

    @Serialization(MyMoshiSerializer::class)
    @Cache(pipeline = MyPipeline5::class, strategy = Strategy.SELECT)
    @POST("/test2")
    @FormUrlEncoded
    fun func4(
        @Field("param1") v1: String,
        @Field("param2") v2: String,
        @FieldMap v3: Map<String, String>,
    ): Flow<NetResult<NetResult<Map<Set<Person>,NetResult<List<String>>>>>>

    @Cache(pipeline = MyPipeline5::class)
    @POST("/test3")
    @Multipart
    fun func5(
        @Part("param1") v1: String,
        @Part("param2") v2: String,
        @Part("param3") v3: RequestBody,
        @Part v4: MultipartBody.Part,
        @Part v5: MultipartBody.Part,
        @PartMap v6: Map<String, String>,
    ): Flow<NetResult<NetResult<Map<String,NetResult<List<String>>>>>>

    @Serialization(MyMoshiSerializer::class)
    @State(MyPipeline5::class)
    interface User2 : OnState<NetResult<String>> {

        @POST("/test14")
        fun test15(
            @JsonKey("age") age: Int,
            @JsonKey("height") height: Float,
            @Query("name") name: String,
        ): Flow<NetResult<String>>

        override fun defaultValue(): NetResult<String> {
            return NetResult(code = 4679, data = null, message = "viverra")
        }

    }

}