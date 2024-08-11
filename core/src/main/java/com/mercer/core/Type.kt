package com.mercer.core

import com.mercer.core.Flag.FLAG_DELETE
import com.mercer.core.Flag.FLAG_FORM
import com.mercer.core.Flag.FLAG_MULTIPART
import com.mercer.core.Flag.FLAG_POST
import com.mercer.core.Flag.FLAG_PUT
import com.mercer.core.Flag.FLAG_GET
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Query
import kotlin.reflect.KClass

/**
 * author:  Mercer
 * date:    2023/12/24
 * desc:
 *   追加参数的类型
 */

enum class Type(
    val annotation: KClass<out Annotation>,
    // 条件
    val flags: Array<Int>
) {
    HEADER(Header::class, arrayOf(FLAG_PUT, FLAG_DELETE, FLAG_POST, FLAG_GET)),
    QUERY(Query::class, arrayOf(FLAG_PUT, FLAG_DELETE, FLAG_POST, FLAG_GET)),
    FIELD(Field::class, arrayOf(FLAG_POST or FLAG_FORM)),
    PART(Part::class, arrayOf(FLAG_POST or FLAG_MULTIPART))
}

/*
接口的参数可以分为以下几种类型：
    路径参数（Path Parameters）：用于指定资源路径，通常在URL中直接嵌入，如 /users/{id}，id即为路径参数[2].
    查询参数（Query Parameters）：附加在URL的末尾，以?和&连接，如 ?name=John&age=30[2].
    请求体参数（Body Parameters）：通常用于POST、PUT请求，包含在请求体中，以传递复杂的数据结构或大量数据[3].
    头部参数（Header Parameters）：包含在请求头部，用于传递认证信息、内容类型等，如 Authorization: Bearer <token>[2].
    表单数据（Form Data）：用于提交表单数据，通常是POST请求中的一种特殊形式[6].
    其他数据类型：如字符串、整型、布尔型、浮点型、日期等
*/

/*
// 请求方法注解 [4,4]
retrofit2.http.GET
retrofit2.http.POST
retrofit2.http.PUT
retrofit2.http.DELETE

retrofit2.http.PATCH
retrofit2.http.HEAD
retrofit2.http.OPTIONS
retrofit2.http.HTTP

// 标记类注解 [3]
retrofit2.http.FormUrlEncoded              发送编码表单数据请求，每个键值对需要使用@Filed注解
retrofit2.http.Multipart                   表示发送form-encoded的数据（适用于有文件上传的场景）
retrofit2.http.Streaming                   将返回结果（ResponseBody）转换为流（字节），如果没有使用注解，默认会把数据全部载入到内存中，之后获取数据也是从内存中读取。

// 请求参数注解 [14]
retrofit2.http.Headers                     添加固定的请求头，作用于方法
retrofit2.http.Header                      添加不固定的请求头，作用于方法的参数
retrofit2.http.Body                        非表单请求体如：json
retrofit2.http.Field、FieldMap              Post表单请求字段，需要配合@FromUrlEncoded使用。体现在请求体上
retrofit2.http.Part、PartMap                文件上传，表单字段
retrofit2.http.Query、QueryMap              Get的请求参数字段，体现在URL上
retrofit2.http.QueryName                    与@Query注解相似，只不过参数（@QueryName String filter）会直接追加在路径后面（?filter）
retrofit2.http.Path                         用于URL中的占位符
retrofit2.http.Url                          替换请求路径，会忽略baseUrl
retrofit2.http.Tag                          用于标记请求,标志参数用来给这次请求打个Tag
retrofit2.http.HeaderMap                    标志参数是一个Map集合的请求头
*/