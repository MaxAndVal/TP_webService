package com.example.lpiem.rickandmortyapp

import android.content.Context
import android.widget.Toast

class GalleryManager private constructor(context: Context) {
    init {

    }

    companion object : SingletonHolder<GalleryManager, Context>(::GalleryManager)

    fun doStuff(context: Context) {
        Toast.makeText(context, "Wubba Lubba Dub Dub !!!", Toast.LENGTH_SHORT).show()
    }
}