package com.aiper.device.i.model

/**
 * @Created on 2024/11/6
 * @author     Mercer
 * @Description:
 *      浇灌单元提醒
 */
data class IrrigatingReminderSettings(
    val waterShortageReminder: Int, // 缺水提醒：0-不提醒，1-提醒
    val weatherReminder: Int, // 天气提醒：0-不提醒，1-提醒
)