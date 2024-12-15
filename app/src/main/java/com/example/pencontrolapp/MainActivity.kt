package com.example.pencontrolapp

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.pencontrolapp.ui.CustomKeyDialog

class MainActivity : AppCompatActivity() {
    private lateinit var leftRightDistanceEditText: EditText
    private lateinit var upDownDistanceEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var leftRightRadioButton: RadioButton
    private lateinit var upDownRadioButton: RadioButton

    companion object {
        @Volatile
        var isCustomDialogShowing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        leftRightDistanceEditText = findViewById(R.id.leftRightDistanceEditText)
        upDownDistanceEditText = findViewById(R.id.upDownDistanceEditText)
        saveButton = findViewById(R.id.saveButton)
        leftRightRadioButton = findViewById(R.id.leftRightRadioButton)
        upDownRadioButton = findViewById(R.id.upDownRadioButton)

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)

        val savedLeftRightDistance = sharedPref.getInt("swipe_distance_left_right", 60)
        leftRightDistanceEditText.setText(savedLeftRightDistance.toString())

        val savedUpDownDistance = sharedPref.getInt("swipe_distance_up_down", 130)
        upDownDistanceEditText.setText(savedUpDownDistance.toString())

        val savedMode = sharedPref.getString("swipe_mode", "left_right")

        if (savedMode == "up_down") {
            upDownRadioButton.isChecked = true
            leftRightRadioButton.isChecked = false
        } else {
            leftRightRadioButton.isChecked = true
            upDownRadioButton.isChecked = false
        }

        // 手动互斥逻辑
        leftRightRadioButton.setOnClickListener {
            leftRightRadioButton.isChecked = true
            upDownRadioButton.isChecked = false
        }

        upDownRadioButton.setOnClickListener {
            upDownRadioButton.isChecked = true
            leftRightRadioButton.isChecked = false
        }

        saveButton.setOnClickListener {
            val leftRightStr = leftRightDistanceEditText.text.toString()
            val upDownStr = upDownDistanceEditText.text.toString()

            val leftRightDistance = leftRightStr.toIntOrNull()
            val upDownDistance = upDownStr.toIntOrNull()

            if (leftRightDistance == null || leftRightDistance !in 10..1000) {
                showInvalidDistanceDialog()
                return@setOnClickListener
            }
            if (upDownDistance == null || upDownDistance !in 10..1000) {
                showInvalidDistanceDialog()
                return@setOnClickListener
            }

            sharedPref.edit().putInt("swipe_distance_left_right", leftRightDistance).apply()
            sharedPref.edit().putInt("swipe_distance_up_down", upDownDistance).apply()

            val selectedMode = if (upDownRadioButton.isChecked) "up_down" else "left_right"
            sharedPref.edit().putString("swipe_mode", selectedMode).apply()

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.save_settings))
                .setMessage(getString(R.string.settings_saved_successfully))
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        val customKeysButton = findViewById<Button>(R.id.customKeysButton)
        customKeysButton.setOnClickListener {
            val dialog = CustomKeyDialog(this)
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
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

    private fun showInvalidDistanceDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.invalid_distance))
            .setMessage(getString(R.string.invalid_distance))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
}
