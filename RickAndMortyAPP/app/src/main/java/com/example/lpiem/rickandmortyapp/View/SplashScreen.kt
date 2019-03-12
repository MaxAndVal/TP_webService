package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash_screen.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashScreen : AppCompatActivity() {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(this).instance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val resultCall = rickAndMortyAPI!!.herokuAwaking()
        callRetrofit(resultCall, RetrofitCallTypes.HEROKU_VOID)

        Picasso.get().load(R.drawable.splash_screen2).fit().centerCrop().into(iv_splashScreen)


        val handler = Handler()
        handler.postDelayed({
            val mainActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }, 2500L)

    }

    private fun <Void> callRetrofit(call: Call<Void>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "F : Heroku should be awake"+t.message.toString())
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, " R : Heroku should be awake")
            }
        })
    }
}