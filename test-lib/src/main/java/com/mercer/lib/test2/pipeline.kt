package com.mercer.lib.test2

import com.mercer.core.Path
import com.mercer.core.Pipeline
import com.mercer.lib.model.NetResult

/**
 * author:  Mercer
 * date:    2024/8/10
 * desc:
 *   缓存
 */
class MyPipeline1: Pipeline<NetResult<String>> {
    override suspend fun read(path: Path): NetResult<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<String>?) {
        TODO("Not yet implemented")
    }
}

object MyPipeline2: Pipeline<NetResult<Int>?> {
    override suspend fun read(path: Path): NetResult<Int>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<Int>?) {
        TODO("Not yet implemented")
    }
}

class MyPipeline3: Pipeline<NetResult<Number>> {
    override suspend fun read(path: Path): NetResult<Number>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<Number>?) {
        TODO("Not yet implemented")
    }
}

class MyPipeline4: Pipeline<NetResult<Int>?> {
    override suspend fun read(path: Path): NetResult<Int>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<Int>?) {
        TODO("Not yet implemented")
    }
}

class MyPipeline5: Pipeline<String> {
    override suspend fun read(path: Path): String? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value:String?) {
        TODO("Not yet implemented")
    }
}