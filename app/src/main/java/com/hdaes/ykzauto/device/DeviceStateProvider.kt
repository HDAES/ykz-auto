package com.hdaes.ykzauto.device

/** Boundary for future lock-screen, power and vendor-specific device checks. */
interface DeviceStateProvider {
    val isDeviceLocked: Boolean
    val isIgnoringBatteryOptimizations: Boolean
}
