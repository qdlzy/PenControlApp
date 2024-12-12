package com.example.pencontrolapp

import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.AccessibilityService
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var distanceEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        distanceEditText = findViewById(R.id.distanceEditText)
        saveButton = findViewById(R.id.saveButton)

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedDistance = sharedPref.getInt("swipe_distance", 700)
        distanceEditText.setText(savedDistance.toString())

        saveButton.setOnClickListener {
            val distanceStr = distanceEditText.text.toString()
            val distance = distanceStr.toIntOrNull() ?: 700
            sharedPref.edit().putInt("swipe_distance", distance).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次返回界面时，检查无障碍服务是否已启用
        if (!isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.accessibility_not_enabled_title))
                .setMessage(getString(R.string.accessibility_not_enabled_message))
                .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                    openAccessibilitySettings(this)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val componentName = enabledService.resolveInfo.serviceInfo.packageName + "/" + enabledService.resolveInfo.serviceInfo.name
            val serviceName = context.packageName + "/" + service.name
            if (componentName == serviceName) {
                return true
            }
        }
        return false
    }

    private fun openAccessibilitySettings(context: Context) {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
