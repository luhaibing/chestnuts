package com.mercer.library.test

import com.mercer.core.Path
import com.mercer.core.Provider


abstract class Mock

class MyStringProvider : Mock(), Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyIntProvider : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider1 : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider2 : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider3 : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider4 : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider5 : Provider<Any> {
    override fun provide(path: Path, key: String): Any? {
        TODO("Not yet implemented")
    }
}