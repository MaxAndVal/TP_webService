package com.example.lpiem.rickandmortyapp.Data.Helpers

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelper constructor(private var context: Context){
    companion object {
        private const val DEVICE_TOKEN = "data.source.prefs.DEVICE_TOKEN"
    }
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    // save device token
    var deviceToken: String = preferences.getString(DEVICE_TOKEN, "")
        set(value) {
            preferences.edit().putString(DEVICE_TOKEN, value).commit()
        }
}