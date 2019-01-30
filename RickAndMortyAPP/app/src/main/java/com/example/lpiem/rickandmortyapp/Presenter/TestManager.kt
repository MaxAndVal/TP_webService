package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context

class TestManager private constructor(private val context: Context) {


    companion object: SingletonHolder<TestManager, Context>(::TestManager)




}