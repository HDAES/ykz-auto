package com.hdaes.ykzauto.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdaes.ykzauto.ui.theme.YkzAutoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val completedModules = listOf(
        "统一配置模型与安全默认值",
        "ConfigRepository 配置访问边界",
        "Preferences DataStore 仓库实现",
        "星期与时间段调度模型",
        "通知、无障碍、工作流、设备和日志模块边界"
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "YKZ Auto") })
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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "第 1 步：架构骨架完成",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "通知监听和自动点击尚未实现。夜间值守默认关闭。",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            items(completedModules) { module ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = module,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    YkzAutoTheme {
        HomeScreen()
    }
}
