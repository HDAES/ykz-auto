package com.hdaes.ykzauto.app

import android.app.Application
import com.hdaes.ykzauto.config.ConfigRepository
import com.hdaes.ykzauto.config.DataStoreConfigRepository

class YkzAutoApplication : Application() {
    val configRepository: ConfigRepository by lazy {
        DataStoreConfigRepository(applicationContext)
    }
}
