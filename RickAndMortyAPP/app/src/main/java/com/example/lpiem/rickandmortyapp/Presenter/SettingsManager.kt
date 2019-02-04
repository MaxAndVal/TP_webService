package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context

class SettingsManager private constructor(private val context: Context) {
    fun fragmentFAQ() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


}