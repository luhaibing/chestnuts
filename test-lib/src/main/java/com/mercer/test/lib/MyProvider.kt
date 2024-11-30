package com.mercer.test.lib

import com.mercer.core.Path
import com.mercer.core.Provider

/**
 * @author :Mercer
 * @Created on 2024/10/19.
 * @Description:
 *   追加参数提供
 */
class MyProvider1 : Provider<Int> {
    override fun provide(path: Path, key: String): Int? {
        TODO("Not yet implemented")
    }
}
object MyProvider2 : Provider<Boolean> {
    override fun provide(path: Path, key: String): Boolean? {
        TODO("Not yet implemented")
    }
}
class MyProvider4 : Provider<String> {
    override fun provide(path: Path, key: String): String? {
        TODO("Not yet implemented")
    }
}