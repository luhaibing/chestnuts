package com.mercer.lib.test2

import com.mercer.core.GsonSerializer
import com.mercer.core.MoshiSerializer
import com.mercer.core.Type

/**
 * author:  Mercer
 * date:    2024/8/11
 * desc:
 *   序列化
 */
class MyGsonSerializer : GsonSerializer {
    override fun <T> deserialize(value: String?, type: java.lang.reflect.Type): T {
        TODO("Not yet implemented")
    }

    override fun <T> serialize(value: T?): String? {
        TODO("Not yet implemented")
    }
}

object MyMoshiSerializer : MoshiSerializer {
    override fun <T> deserialize(value: String?, type: java.lang.reflect.Type): T {
        TODO("Not yet implemented")
    }

    override fun <T> serialize(value: T?): String? {
        TODO("Not yet implemented")
    }

}