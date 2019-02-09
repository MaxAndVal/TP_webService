package com.example.lpiem.rickandmortyapp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_open_deck.*

class OpenDeckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)

        tv_openYourDeck.text = "You have "+
    }



}
