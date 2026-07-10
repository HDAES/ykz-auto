package com.hdaes.ykzauto.workflow

data class TargetElementRule(
    val text: String,
    val exactTextMatch: Boolean,
    val requireClickable: Boolean,
    val requireEnabled: Boolean
) {
    companion object {
        val DEFAULT = TargetElementRule(
            text = "",
            exactTextMatch = true,
            requireClickable = true,
            requireEnabled = true
        )
    }
}
