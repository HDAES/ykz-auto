package com.hdaes.ykzauto.permissions

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hdaes.ykzauto.accessibility.YkzAccessibilityService
import com.hdaes.ykzauto.device.AndroidDeviceStateProvider
import com.hdaes.ykzauto.device.DeviceStateProvider
import com.hdaes.ykzauto.notification.YkzNotificationListenerService

data class PermissionStatus(
    val notificationListenerEnabled: Boolean,
    val accessibilityServiceEnabled: Boolean,
    val appNotificationsEnabled: Boolean,
    val batteryOptimizationIgnored: Boolean,
    val isXiaomiFamilyDevice: Boolean,
    val isDeviceUnlockedAndInteractive: Boolean,
    val deviceBrandLabel: String
)

interface PermissionStatusProvider {
    fun getStatus(): PermissionStatus
}

class AndroidPermissionStatusProvider(
    context: Context,
    private val deviceStateProvider: DeviceStateProvider = AndroidDeviceStateProvider(context)
) : PermissionStatusProvider {
    private val appContext = context.applicationContext
    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)
    private val accessibilityManager = appContext.getSystemService(AccessibilityManager::class.java)

    override fun getStatus(): PermissionStatus = PermissionStatus(
        notificationListenerEnabled = isNotificationListenerEnabled(),
        accessibilityServiceEnabled = isAccessibilityServiceEnabled(),
        appNotificationsEnabled = areAppNotificationsEnabled(),
        batteryOptimizationIgnored = deviceStateProvider.isIgnoringBatteryOptimizations,
        isXiaomiFamilyDevice = deviceStateProvider.isXiaomiFamilyDevice,
        isDeviceUnlockedAndInteractive = deviceStateProvider.isDeviceUnlockedAndInteractive,
        deviceBrandLabel = deviceStateProvider.deviceBrandLabel
    )

    private fun isNotificationListenerEnabled(): Boolean {
        val listenerComponent = ComponentName(
            appContext,
            YkzNotificationListenerService::class.java
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            notificationManager.isNotificationListenerAccessGranted(listenerComponent)
        } else {
            appContext.packageName in NotificationManagerCompat.getEnabledListenerPackages(appContext)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponent = ComponentName(
            appContext,
            YkzAccessibilityService::class.java
        )
        return accessibilityManager
            .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { service ->
                val serviceInfo = service.resolveInfo.serviceInfo
                ComponentName(serviceInfo.packageName, serviceInfo.name) == expectedComponent
            }
    }

    private fun areAppNotificationsEnabled(): Boolean {
        val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        return runtimePermissionGranted &&
            NotificationManagerCompat.from(appContext).areNotificationsEnabled()
    }
}
