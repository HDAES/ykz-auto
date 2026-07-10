package com.hdaes.ykzauto.config

import com.hdaes.ykzauto.notification.NotificationRule
import com.hdaes.ykzauto.schedule.TimeWindow
import com.hdaes.ykzauto.schedule.WeekdayRule
import com.hdaes.ykzauto.workflow.WorkflowConfig

data class AppConfig(
    val targetAppPackageName: String,
    val notificationRule: NotificationRule,
    val workflowConfig: WorkflowConfig,
    val weekdayRule: WeekdayRule,
    val allowedTimeWindows: List<TimeWindow>,
    val enableNightWatch: Boolean,
    val enableNodeTreeLogging: Boolean
) {
    companion object {
        val DEFAULT = AppConfig(
            targetAppPackageName = "",
            notificationRule = NotificationRule.DEFAULT,
            workflowConfig = WorkflowConfig.DEFAULT,
            weekdayRule = WeekdayRule.ALL_DAYS,
            allowedTimeWindows = listOf(TimeWindow.ALL_DAY),
            enableNightWatch = false,
            enableNodeTreeLogging = false
        )
    }
}
