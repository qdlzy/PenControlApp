package com.example.pencontrolapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("MyAccessibilityService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不需要额外处理事件
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

    private fun getSwipeDistance(): Int {
        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPref.getInt("swipe_distance", 700) // 默认700像素
    }

    private fun performSwipeForward() {
        val distance = getSwipeDistance()
        val startX = 300f
        val startY = 500f
        val endX = startX + distance
        val endY = 500f

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Forward swipe gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Forward swipe gesture cancelled")
            }
        }, null)
    }

    private fun performSwipeBackward() {
        val distance = getSwipeDistance()
        val startX = 1000f
        val startY = 500f
        val endX = startX - distance
        val endY = 500f

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Backward swipe gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Backward swipe gesture cancelled")
            }
        }, null)
    }
}
