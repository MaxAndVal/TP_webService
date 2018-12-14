package com.example.lpiem.rickandmortyapp

import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val handler = Handler()
        handler.postDelayed({
            val mainActivityIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }, 2000L)

    }


}
