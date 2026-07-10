package com.hdaes.ykzauto.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    state: PermissionsUiState,
    onRefresh: () -> Unit,
    onOpenNotificationAccess: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenAppDetails: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatteryOptimization: () -> Unit,
    onOpenXiaomiAutostart: () -> Unit,
    onOpenXiaomiBatteryPolicy: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "权限与设备状态") },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text(text = "刷新")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SummaryCard(state)
            }

            item {
                PermissionStatusCard(
                    title = "通知使用权",
                    description = "允许系统把其他 App 的通知事件交给 YKZ Auto。",
                    granted = state.notificationAccessGranted,
                    actionLabel = "打开通知使用权设置",
                    onAction = onOpenNotificationAccess
                )
            }

            item {
                PermissionStatusCard(
                    title = "辅助功能服务",
                    description = "后续用于读取页面节点、滚动和点击；当前服务没有自动操作逻辑。",
                    granted = state.accessibilityAccessGranted,
                    actionLabel = "打开辅助功能设置",
                    onAction = onOpenAccessibilitySettings
                )
            }

            item {
                PermissionStatusCard(
                    title = "App 通知权限",
                    description = "允许 YKZ Auto 显示运行状态、失败和人工处理提醒。",
                    granted = state.appNotificationsEnabled,
                    actionLabel = "打开通知设置页",
                    onAction = onOpenNotificationSettings
                )
            }

            item {
                PermissionStatusCard(
                    title = "忽略电池优化",
                    description = "夜间值守需要尽量避免系统冻结或延迟后台任务。",
                    granted = state.batteryOptimizationIgnored,
                    actionLabel = "打开电池优化设置",
                    onAction = onOpenBatteryOptimization
                )
            }

            item {
                DeviceStatusCard(state)
            }

            item {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenAppDetails
                ) {
                    Text(text = "打开 App 详情页")
                }
            }

            item {
                XiaomiGuideCard(
                    isXiaomiFamilyDevice = state.isXiaomiFamilyDevice,
                    onOpenAutostart = onOpenXiaomiAutostart,
                    onOpenBatteryPolicy = onOpenXiaomiBatteryPolicy
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(state: PermissionsUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "必要权限 ${state.grantedRequiredCount}/${PermissionsUiState.REQUIRED_PERMISSION_COUNT}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = state.deviceBrandLabel,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "从系统设置返回后页面会自动重新检测，也可以手动刷新。",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PermissionStatusCard(
    title: String,
    description: String,
    granted: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusHeader(title = title, granted = granted)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAction
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

@Composable
private fun StatusHeader(title: String, granted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = if (granted) "已开启" else "未开启",
            color = if (granted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun DeviceStatusCard(state: PermissionsUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "设备状态",
                style = MaterialTheme.typography.titleMedium
            )
            InformationalStatusRow(
                label = "Xiaomi / Redmi / POCO",
                positive = state.isXiaomiFamilyDevice,
                positiveText = "是",
                negativeText = "否"
            )
            InformationalStatusRow(
                label = "已解锁且可交互",
                positive = state.isDeviceUnlockedAndInteractive,
                positiveText = "可执行",
                negativeText = "锁定或息屏"
            )
        }
    }
}

@Composable
private fun InformationalStatusRow(
    label: String,
    positive: Boolean,
    positiveText: String,
    negativeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = if (positive) positiveText else negativeText,
            color = if (positive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun XiaomiGuideCard(
    isXiaomiFamilyDevice: Boolean,
    onOpenAutostart: () -> Unit,
    onOpenBatteryPolicy: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "小米 / Redmi / POCO 人工设置",
                style = MaterialTheme.typography.titleMedium
            )
            if (!isXiaomiFamilyDevice) {
                Text(
                    text = "当前未检测到小米系设备，以下操作通常不需要执行。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "1. 在自启动管理中允许 YKZ Auto 自启动。\n" +
                    "2. 将本 App 的省电策略设为“无限制”。\n" +
                    "3. 打开最近任务，长按本 App 卡片并启用锁定。",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenAutostart
            ) {
                Text(text = "打开小米自启动设置")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenBatteryPolicy
            ) {
                Text(text = "打开小米省电策略设置")
            }
            Text(
                text = "私有页面不可用时会自动降级到 App 详情页，再失败则打开系统设置首页。",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
