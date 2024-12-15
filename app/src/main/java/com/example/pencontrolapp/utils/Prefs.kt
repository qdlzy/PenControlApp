package com.example.pencontrolapp.utils

import android.content.Context
import androidx.preference.PreferenceManager

fun Context.getPrefString(key: String, defValue: String? = null): String? {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    return sp.getString(key, defValue)
}

fun Context.putPrefString(key: String, value: String?) {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    sp.edit().putString(key, value).apply()
}
