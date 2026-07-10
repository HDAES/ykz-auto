package com.hdaes.ykzauto.workflow

data class WorkflowConfig(
    val pageLoadKeywords: Set<String>,
    val targetButtonRule: TargetElementRule,
    val confirmButtonRule: TargetElementRule,
    val maxScrollCount: Int,
    val stepTimeoutMillis: Long,
    val overallTimeoutMillis: Long
) {
    init {
        require(maxScrollCount >= 0)
        require(stepTimeoutMillis > 0)
        require(overallTimeoutMillis >= stepTimeoutMillis)
    }

    companion object {
        val DEFAULT = WorkflowConfig(
            pageLoadKeywords = emptySet(),
            targetButtonRule = TargetElementRule.DEFAULT,
            confirmButtonRule = TargetElementRule.DEFAULT,
            maxScrollCount = 8,
            stepTimeoutMillis = 10_000L,
            overallTimeoutMillis = 90_000L
        )
    }
}
