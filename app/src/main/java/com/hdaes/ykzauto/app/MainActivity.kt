package com.hdaes.ykzauto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.hdaes.ykzauto.permissions.AndroidPermissionStatusProvider
import com.hdaes.ykzauto.permissions.PermissionStatusProvider
import com.hdaes.ykzauto.permissions.SettingsNavigator
import com.hdaes.ykzauto.ui.permissions.PermissionsScreen
import com.hdaes.ykzauto.ui.permissions.PermissionsUiState
import com.hdaes.ykzauto.ui.theme.YkzAutoTheme

class MainActivity : ComponentActivity() {
    private lateinit var permissionStatusProvider: PermissionStatusProvider
    private lateinit var settingsNavigator: SettingsNavigator
    private val permissionsUiState = mutableStateOf(PermissionsUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionStatusProvider = AndroidPermissionStatusProvider(this)
        settingsNavigator = SettingsNavigator(this)
        refreshPermissionStatus()

        enableEdgeToEdge()
        setContent {
            YkzAutoTheme {
                PermissionsScreen(
                    state = permissionsUiState.value,
                    onRefresh = ::refreshPermissionStatus,
                    onOpenNotificationAccess = settingsNavigator::openNotificationListenerSettings,
                    onOpenAccessibilitySettings = settingsNavigator::openAccessibilitySettings,
                    onOpenAppDetails = settingsNavigator::openAppDetailsSettings,
                    onOpenNotificationSettings = settingsNavigator::openAppNotificationSettings,
                    onOpenBatteryOptimization = settingsNavigator::openBatteryOptimizationSettings,
                    onOpenXiaomiAutostart = settingsNavigator::openXiaomiAutostartSettings,
                    onOpenXiaomiBatteryPolicy = settingsNavigator::openXiaomiBatteryPolicySettings
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::permissionStatusProvider.isInitialized) {
            refreshPermissionStatus()
        }
    }

    private fun refreshPermissionStatus() {
        permissionsUiState.value = PermissionsUiState.from(
            permissionStatusProvider.getStatus()
        )
    }
}
