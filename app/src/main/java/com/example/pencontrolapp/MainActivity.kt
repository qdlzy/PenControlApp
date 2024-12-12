package com.example.pencontrolapp

import android.content.Context
import android.os.Bundle
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
}
