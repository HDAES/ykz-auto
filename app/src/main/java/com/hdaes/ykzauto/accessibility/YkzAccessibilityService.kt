package com.hdaes.ykzauto.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Permission-registration shell only.
 * Node inspection, scrolling and clicking will be implemented in a later phase.
 */
class YkzAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}
