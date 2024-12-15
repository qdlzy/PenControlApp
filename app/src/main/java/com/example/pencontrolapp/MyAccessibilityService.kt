package com.example.pencontrolapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.example.pencontrolapp.utils.getPrefString

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("MyAccessibilityService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不需要事件处理
    }

    override fun onInterrupt() {
        // 无需实现
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // 如果自定义对话框正在显示，则不处理滑动逻辑
        if (MainActivity.isCustomDialogShowing) {
            return super.onKeyEvent(event)
        }

        if (event.action == KeyEvent.ACTION_DOWN) {
            val settings = getSwipeSettings()
            val (prevKeys, nextKeys) = getCustomKeys(this)

            val keyCode = event.keyCode
            if (prevKeys.contains(keyCode)) {
                if (settings.mode == "left_right") {
                    performHorizontalSwipe(isForward = false, distance = settings.distanceLeftRight)
                } else {
                    performVerticalSwipe(isForward = true, distance = settings.distanceUpDown) // 下滑
                }
                return true
            }

            if (nextKeys.contains(keyCode)) {
                if (settings.mode == "left_right") {
                    performHorizontalSwipe(isForward = true, distance = settings.distanceLeftRight)
                } else {
                    performVerticalSwipe(isForward = false, distance = settings.distanceUpDown) // 上滑
                }
                return true
            }

            // 原本的PAGE_UP / PAGE_DOWN逻辑
            when (event.keyCode) {
                KeyEvent.KEYCODE_PAGE_UP -> {
                    if (settings.mode == "left_right") {
                        Log.d("MyAccessibilityService", "Left Swipe (left_right mode)")
                        performHorizontalSwipe(isForward = false, distance = settings.distanceLeftRight)
                    } else {
                        Log.d("MyAccessibilityService", "Down Swipe (up_down mode)")
                        performVerticalSwipe(isForward = true, distance = settings.distanceUpDown)
                    }
                    return true
                }
                KeyEvent.KEYCODE_PAGE_DOWN -> {
                    if (settings.mode == "left_right") {
                        Log.d("MyAccessibilityService", "Right Swipe (left_right mode)")
                        performHorizontalSwipe(isForward = true, distance = settings.distanceLeftRight)
                    } else {
                        Log.d("MyAccessibilityService", "Up Swipe (up_down mode)")
                        performVerticalSwipe(isForward = false, distance = settings.distanceUpDown)
                    }
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun getSwipeSettings(): SwipeSettings {
        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val mode = sharedPref.getString("swipe_mode", "left_right") ?: "left_right"
        val distanceLeftRight = sharedPref.getInt("swipe_distance_left_right", 60)
        val distanceUpDown = sharedPref.getInt("swipe_distance_up_down", 130)
        return SwipeSettings(mode, distanceLeftRight, distanceUpDown)
    }

    private fun getCustomKeys(context: Context): Pair<List<Int>, List<Int>> {
        val prevKeysStr = context.getPrefString("customPrevKey", "") ?: ""
        val nextKeysStr = context.getPrefString("customNextKey", "") ?: ""
        val prevKeys = prevKeysStr.split(",").mapNotNull { it.toIntOrNull() }
        val nextKeys = nextKeysStr.split(",").mapNotNull { it.toIntOrNull() }
        return Pair(prevKeys, nextKeys)
    }

    private fun performHorizontalSwipe(isForward: Boolean, distance: Int) {
        val startX = if (isForward) 300f else 1000f
        val endX = if (isForward) startX + distance else startX - distance
        val startY = 500f
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
                Log.d("MyAccessibilityService", "Horizontal swipe gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Horizontal swipe gesture cancelled")
            }
        }, null)
    }

    private fun performVerticalSwipe(isForward: Boolean, distance: Int) {
        val startX = 500f
        val startY = if (isForward) 300f else 1000f
        val endY = if (isForward) startY + distance else startY - distance
        val endX = 500f

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Vertical swipe gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                Log.d("MyAccessibilityService", "Vertical swipe gesture cancelled")
            }
        }, null)
    }

    data class SwipeSettings(val mode: String, val distanceLeftRight: Int, val distanceUpDown: Int)
}
