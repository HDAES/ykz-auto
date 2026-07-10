package com.hdaes.ykzauto.device

interface DeviceStateProvider {
    val isIgnoringBatteryOptimizations: Boolean
    val isXiaomiFamilyDevice: Boolean
    val isDeviceUnlockedAndInteractive: Boolean
    val deviceBrandLabel: String
}
