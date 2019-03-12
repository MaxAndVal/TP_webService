package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Picasso.get().load(R.drawable.splash_screen2).fit().centerCrop().into(iv_splashScreen)

        val handler = Handler()
        handler.postDelayed({
            val mainActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }, 2500L)

    }


}
