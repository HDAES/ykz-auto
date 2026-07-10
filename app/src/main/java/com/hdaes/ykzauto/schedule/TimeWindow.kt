package com.hdaes.ykzauto.schedule

import java.time.LocalTime

data class TimeWindow(
    val startMinuteInclusive: Int,
    val endMinuteInclusive: Int
) {
    init {
        require(startMinuteInclusive in MIN_MINUTE..MAX_MINUTE)
        require(endMinuteInclusive in MIN_MINUTE..MAX_MINUTE)
    }

    val crossesMidnight: Boolean
        get() = startMinuteInclusive > endMinuteInclusive

    fun contains(time: LocalTime): Boolean = containsMinute(
        minuteOfDay = time.hour * MINUTES_PER_HOUR + time.minute
    )

    fun containsMinute(minuteOfDay: Int): Boolean {
        require(minuteOfDay in MIN_MINUTE..MAX_MINUTE)
        return if (crossesMidnight) {
            minuteOfDay >= startMinuteInclusive || minuteOfDay <= endMinuteInclusive
        } else {
            minuteOfDay in startMinuteInclusive..endMinuteInclusive
        }
    }

    companion object {
        private const val MINUTES_PER_HOUR = 60
        private const val MIN_MINUTE = 0
        private const val MAX_MINUTE = 23 * MINUTES_PER_HOUR + 59

        val ALL_DAY = TimeWindow(MIN_MINUTE, MAX_MINUTE)

        fun of(
            startHour: Int,
            startMinute: Int,
            endHour: Int,
            endMinute: Int
        ) = TimeWindow(
            startMinuteInclusive = startHour * MINUTES_PER_HOUR + startMinute,
            endMinuteInclusive = endHour * MINUTES_PER_HOUR + endMinute
        )
    }
}
