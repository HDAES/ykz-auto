package com.hdaes.ykzauto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hdaes.ykzauto.ui.home.HomeScreen
import com.hdaes.ykzauto.ui.theme.YkzAutoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YkzAutoTheme {
                HomeScreen()
            }
        }
    }
}
