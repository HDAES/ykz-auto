package com.hdaes.ykzauto.device

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import java.util.Locale

class AndroidDeviceStateProvider(context: Context) : DeviceStateProvider {
    private val appContext = context.applicationContext
    private val powerManager = appContext.getSystemService(PowerManager::class.java)
    private val keyguardManager = appContext.getSystemService(KeyguardManager::class.java)

    override val isIgnoringBatteryOptimizations: Boolean
        get() = powerManager.isIgnoringBatteryOptimizations(appContext.packageName)

    override val isXiaomiFamilyDevice: Boolean
        get() {
            val identity = listOf(Build.MANUFACTURER, Build.BRAND)
                .joinToString(separator = " ")
                .lowercase(Locale.ROOT)
            return XIAOMI_FAMILY_NAMES.any(identity::contains)
        }

    override val isDeviceUnlockedAndInteractive: Boolean
        get() = powerManager.isInteractive && !keyguardManager.isDeviceLocked

    override val deviceBrandLabel: String
        get() = listOf(Build.MANUFACTURER, Build.BRAND, Build.MODEL)
            .filter(String::isNotBlank)
            .distinct()
            .joinToString(separator = " · ")

    private companion object {
        val XIAOMI_FAMILY_NAMES = setOf("xiaomi", "redmi", "poco")
    }
}
