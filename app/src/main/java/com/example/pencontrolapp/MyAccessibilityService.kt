package com.example.pencontrolapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("MyAccessibilityService", "Accessibility service connected")
        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不需要处理特定AccessibilityEvent
    }

    override fun onInterrupt() {
        // 中断处理，无需实现
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_PAGE_DOWN -> {
                    Log.d("MyAccessibilityService", "Fast Forward triggered")
                    performSwipeForward()
                    return true
                }
                KeyEvent.KEYCODE_PAGE_UP -> {
                    Log.d("MyAccessibilityService", "Rewind triggered")
                    performSwipeBackward()
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun performSwipeForward() {
        // 模拟手势：从左向右滑动
        val path = Path().apply {
            moveTo(300f, 500f)
            lineTo(1000f, 500f)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, null, null)
    }

    private fun performSwipeBackward() {
        // 模拟手势：从右向左滑动
        val path = Path().apply {
            moveTo(1000f, 500f)
            lineTo(300f, 500f)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, null, null)
    }
}
