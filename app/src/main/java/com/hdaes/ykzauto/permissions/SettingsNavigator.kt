package com.hdaes.ykzauto.permissions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class SettingsNavigator(context: Context) {
    private val appContext = context.applicationContext
    private val packageName = appContext.packageName
    private val packageUri = Uri.parse("package:$packageName")
    private val appLabel by lazy {
        appContext.packageManager
            .getApplicationLabel(appContext.applicationInfo)
            .toString()
    }

    fun openNotificationListenerSettings() {
        openStandardSettings(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    fun openAccessibilitySettings() {
        openStandardSettings(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun openAppDetailsSettings() {
        if (!tryStart(appDetailsIntent())) {
            tryStart(systemSettingsIntent())
        }
    }

    fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        openStandardSettings(intent)
    }

    fun openBatteryOptimizationSettings() {
        val directRequest = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            packageUri
        )
        val optimizationList = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        openStandardSettings(directRequest, optimizationList)
    }

    fun openXiaomiAutostartSettings() {
        openXiaomiPrivateSettings(
            Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                putExtra("extra_pkgname", packageName)
            },
            Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
        )
    }

    fun openXiaomiBatteryPolicySettings() {
        openXiaomiPrivateSettings(
            xiaomiHiddenAppsIntent(
                ownerPackage = "com.miui.securitycenter",
                activityName = "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            ),
            xiaomiHiddenAppsIntent(
                ownerPackage = "com.miui.powerkeeper",
                activityName = "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
        )
    }

    private fun xiaomiHiddenAppsIntent(
        ownerPackage: String,
        activityName: String
    ) = Intent().apply {
        component = ComponentName(ownerPackage, activityName)
        putExtra("package_name", packageName)
        putExtra("package_label", appLabel)
    }

    private fun openStandardSettings(vararg primaryIntents: Intent) {
        if (primaryIntents.any(::tryStart)) return
        if (tryStart(appDetailsIntent())) return
        tryStart(systemSettingsIntent())
    }

    /**
     * Xiaomi/Redmi/POCO private pages are not stable APIs. The required order is:
     * concrete vendor page -> app details -> system settings home.
     */
    private fun openXiaomiPrivateSettings(vararg vendorIntents: Intent) {
        if (vendorIntents.any(::tryStart)) return
        if (tryStart(appDetailsIntent())) return
        tryStart(systemSettingsIntent())
    }

    private fun appDetailsIntent() = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        packageUri
    )

    private fun systemSettingsIntent() = Intent(Settings.ACTION_SETTINGS)

    private fun tryStart(intent: Intent): Boolean = try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
        true
    } catch (_: RuntimeException) {
        false
    }
}
