package com.hdaes.ykzauto.schedule

import com.hdaes.ykzauto.config.AppConfig
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleManagerTest {
    private val manager = ScheduleManager()

    @Test
    fun overnightWindowIncludesLateNightAndEarlyMorning() {
        val window = TimeWindow.of(22, 0, 7, 0)

        assertTrue(window.containsMinute(23 * 60))
        assertTrue(window.containsMinute(6 * 60 + 30))
        assertFalse(window.containsMinute(12 * 60))
    }

    @Test
    fun nightWatchMustBeEnabledBeforeScheduleAllowsExecution() {
        val mondayAtNoon = ZonedDateTime.of(
            2026,
            7,
            6,
            12,
            0,
            0,
            0,
            ZoneId.of("Europe/Berlin")
        )

        assertFalse(manager.isExecutionAllowed(AppConfig.DEFAULT, mondayAtNoon))
        assertTrue(
            manager.isExecutionAllowed(
                AppConfig.DEFAULT.copy(enableNightWatch = true),
                mondayAtNoon
            )
        )
    }
}
