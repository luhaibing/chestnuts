package com.mercer.lib.test2

import com.mercer.core.CachePipeline
import com.mercer.core.Path
import com.mercer.lib.model.NetResult

/**
 * author:  Mercer
 * date:    2024/7/28
 * desc:
 *
 */
interface AbsCachePipeline : CachePipeline<NetResult<String>>

//class SimpleCachePipeline {
class SimpleCachePipeline : CachePipeline<NetResult<String>> {
    override suspend fun read(path: Path): NetResult<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<String>?) {
        TODO("Not yet implemented")
    }
}

data object SimpleCachePipeline2 : AbsCachePipeline {

    override suspend fun read(path: Path): NetResult<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun write(path: Path, value: NetResult<String>?) {
        TODO("Not yet implemented")
    }

}