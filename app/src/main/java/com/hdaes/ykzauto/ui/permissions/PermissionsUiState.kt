package com.hdaes.ykzauto.ui.permissions

import com.hdaes.ykzauto.permissions.PermissionStatus

data class PermissionsUiState(
    val notificationAccessGranted: Boolean = false,
    val accessibilityAccessGranted: Boolean = false,
    val appNotificationsEnabled: Boolean = false,
    val batteryOptimizationIgnored: Boolean = false,
    val isXiaomiFamilyDevice: Boolean = false,
    val isDeviceUnlockedAndInteractive: Boolean = false,
    val deviceBrandLabel: String = "未知设备"
) {
    val grantedRequiredCount: Int
        get() = listOf(
            notificationAccessGranted,
            accessibilityAccessGranted,
            appNotificationsEnabled,
            batteryOptimizationIgnored
        ).count(Boolean::not).let { REQUIRED_PERMISSION_COUNT - it }

    companion object {
        const val REQUIRED_PERMISSION_COUNT = 4

        fun from(status: PermissionStatus) = PermissionsUiState(
            notificationAccessGranted = status.notificationListenerEnabled,
            accessibilityAccessGranted = status.accessibilityServiceEnabled,
            appNotificationsEnabled = status.appNotificationsEnabled,
            batteryOptimizationIgnored = status.batteryOptimizationIgnored,
            isXiaomiFamilyDevice = status.isXiaomiFamilyDevice,
            isDeviceUnlockedAndInteractive = status.isDeviceUnlockedAndInteractive,
            deviceBrandLabel = status.deviceBrandLabel.ifBlank { "未知设备" }
        )
    }
}
