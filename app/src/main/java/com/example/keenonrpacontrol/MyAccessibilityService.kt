package com.example.keenonrpacontrol

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events here if needed
    }

    override fun onInterrupt() {
        // Handle service interruptions here if needed
    }

    fun performClick(node: AccessibilityNodeInfo?) {
        node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    fun performTap(x: Int, y: Int) {
        // Logic to perform tap at coordinates (x, y)
        // Use AccessibilityNodeInfo to find the element or use performGlobalAction for certain actions
    }

    fun performSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        // Logic to perform swipe from (startX, startY) to (endX, endY)
        // This will depend on how you want to implement it
    }

    fun swipeLeft() {
        performGlobalAction(GLOBAL_ACTION_BACK) // Example action, replace as needed
    }

    fun swipeRight() {
        performGlobalAction(GLOBAL_ACTION_RECENTS) // Example action, replace as needed
    }

    fun performClickOnView(viewId: String) {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val node = findNodeById(rootNode, viewId)
            performClick(node)
        }
    }

    private fun findNodeById(node: AccessibilityNodeInfo, viewId: String): AccessibilityNodeInfo? {
        if (node.viewIdResourceName == viewId) {
            return node
        }
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            val result = findNodeById(childNode, viewId)
            if (result != null) {
                return result
            }
        }
        return null
    }

}
