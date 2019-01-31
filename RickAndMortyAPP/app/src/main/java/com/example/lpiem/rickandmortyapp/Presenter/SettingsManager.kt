package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context

class SettingsManager private constructor(private val context: Context) {

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


}