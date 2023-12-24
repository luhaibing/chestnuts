package com.mercer.lib.test2

import com.mercer.core.Argument
import com.mercer.core.Provider


class MyStringProvider : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        TODO("Not yet implemented")
    }
}

class MyIntProvider : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        TODO("Not yet implemented")
    }

}

class MyStringProvider1 : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        println()
        val toInt = (Math.random() * 100).toInt()
        return toInt
        // TODO("Not yet implemented")
    }
}

class MyStringProvider2 : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        println()
        val toString = (Math.random() * 100).toInt().toString()
        return toString
        // TODO("Not yet implemented")
    }
}

class MyStringProvider3 : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
         TODO("Not yet implemented")
    }
}

class MyStringProvider4 : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        TODO("Not yet implemented")
    }
}

class MyStringProvider5 : Provider {
    override fun provide(vararg args: Argument<*>): Any? {
        TODO("Not yet implemented")
    }
}