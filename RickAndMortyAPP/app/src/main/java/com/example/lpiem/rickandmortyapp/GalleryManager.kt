package com.example.lpiem.rickandmortyapp

import android.content.Context
import android.widget.Toast

class GalleryManager private constructor(private val context: Context) {

    init {

    }

    companion object : SingletonHolder<GalleryManager, Context>(::GalleryManager)

    fun doStuff() {
        Toast.makeText(context, "Wubba Lubba Dub Dub !!!", Toast.LENGTH_SHORT).show()
    }
}