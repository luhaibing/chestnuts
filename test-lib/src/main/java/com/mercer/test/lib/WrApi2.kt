package com.mercer.test.lib

import com.aiper.base.data.protocol.BaseResp
import com.aiper.device.i.model.IrrigatingReminderSettings
import com.mercer.annotate.http.CacheKey
import com.mercer.annotate.http.Decorator
import com.mercer.annotate.http.JsonKey
import com.mercer.annotate.http.Persistence
import com.mercer.annotate.http.Serialization
import com.mercer.core.DefaultPersistenceDispatcher
import kotlinx.coroutines.flow.Flow
import retrofit2.http.POST

/**
 * @author :Mercer
 * @Created on 2025/01/03.
 * @Description:
 *
 */
//@Decorator(RetrofitCreator::class)
//@Serialization(DefaultConverterFactory::class)

@Decorator(SimpleCreator::class)
@Serialization(MoshiConverterFactory::class)
interface WrApi2 {

    @Persistence(MyPersistence::class)
    // @Persistence(DefaultOnPersistence::class, DefaultPersistenceDispatcher::class,)
    @POST("/wr/getReminderSetting")
    fun getR2(
        @CacheKey("sn") @JsonKey("sn") sn: String,
    ): Flow<BaseResp<IrrigatingReminderSettings>>

}