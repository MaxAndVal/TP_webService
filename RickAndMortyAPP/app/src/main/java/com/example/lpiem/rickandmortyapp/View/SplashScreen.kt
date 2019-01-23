package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lpiem.rickandmortyapp.R

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val handler = Handler()
        handler.postDelayed({
            val mainActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(mainActivityIntent)
        }, 2000L)

    }


}
