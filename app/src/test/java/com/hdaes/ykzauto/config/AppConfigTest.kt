package com.hdaes.ykzauto.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppConfigTest {
    @Test
    fun defaultConfigIsSafeAndExplicit() {
        val config = AppConfig.DEFAULT

        assertEquals("", config.targetAppPackageName)
        assertTrue(config.notificationRule.keywords.isEmpty())
        assertTrue(config.workflowConfig.pageLoadKeywords.isEmpty())
        assertEquals(8, config.workflowConfig.maxScrollCount)
        assertEquals(10_000L, config.workflowConfig.stepTimeoutMillis)
        assertEquals(90_000L, config.workflowConfig.overallTimeoutMillis)
        assertFalse(config.enableNightWatch)
        assertFalse(config.enableNodeTreeLogging)
    }
}
