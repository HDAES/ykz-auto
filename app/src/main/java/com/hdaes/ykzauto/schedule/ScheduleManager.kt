package com.hdaes.ykzauto.schedule

import com.hdaes.ykzauto.config.AppConfig
import java.time.Clock
import java.time.ZonedDateTime

class ScheduleManager(
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun isExecutionAllowed(config: AppConfig): Boolean = isExecutionAllowed(
        config = config,
        dateTime = ZonedDateTime.now(clock)
    )

    fun isExecutionAllowed(
        config: AppConfig,
        dateTime: ZonedDateTime
    ): Boolean {
        if (!config.enableNightWatch) return false
        if (!config.weekdayRule.allows(dateTime.dayOfWeek)) return false
        return config.allowedTimeWindows.any { window ->
            window.contains(dateTime.toLocalTime())
        }
    }
}
