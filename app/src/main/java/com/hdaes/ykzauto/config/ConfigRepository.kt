package com.hdaes.ykzauto.config

import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val config: Flow<AppConfig>

    suspend fun replace(config: AppConfig)

    suspend fun update(transform: (AppConfig) -> AppConfig)

    suspend fun reset()
}
