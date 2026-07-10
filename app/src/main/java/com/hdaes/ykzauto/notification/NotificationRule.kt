package com.hdaes.ykzauto.notification

data class NotificationRule(
    val keywords: Set<String>,
    val matchAllKeywords: Boolean,
    val ignoreCase: Boolean
) {
    companion object {
        val DEFAULT = NotificationRule(
            keywords = emptySet(),
            matchAllKeywords = false,
            ignoreCase = true
        )
    }
}
