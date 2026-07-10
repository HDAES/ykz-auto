package com.hdaes.ykzauto.ui.settings

import com.hdaes.ykzauto.config.AppConfig

data class SettingsUiState(
    val config: AppConfig = AppConfig.DEFAULT,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
