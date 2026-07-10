package com.hdaes.ykzauto.ui.permissions

data class PermissionsUiState(
    val notificationAccessGranted: Boolean = false,
    val accessibilityAccessGranted: Boolean = false,
    val batteryOptimizationIgnored: Boolean = false
)
