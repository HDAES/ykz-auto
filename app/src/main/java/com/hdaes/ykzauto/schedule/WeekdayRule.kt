package com.hdaes.ykzauto.schedule

import java.time.DayOfWeek

data class WeekdayRule(
    val allowedDays: Set<DayOfWeek>
) {
    fun allows(dayOfWeek: DayOfWeek): Boolean = dayOfWeek in allowedDays

    companion object {
        val ALL_DAYS = WeekdayRule(DayOfWeek.entries.toSet())
        val NO_DAYS = WeekdayRule(emptySet())
    }
}
