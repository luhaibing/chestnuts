package com.mercer.core

enum class Type {
    // 常用
    HEADER,
    QUERY,

    // TODO:  不常用
    FIELD,
    PART,
    // BODY;
}

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