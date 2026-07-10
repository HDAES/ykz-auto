package com.hdaes.ykzauto.ui.guard

data class GuardUiState(
    val isNightWatchEnabled: Boolean = false,
    val isWithinAllowedSchedule: Boolean = false,
    val blockingReason: String? = null
)
