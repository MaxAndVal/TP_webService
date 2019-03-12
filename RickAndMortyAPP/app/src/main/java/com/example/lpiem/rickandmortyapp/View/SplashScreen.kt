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
        callRetrofit(resultCall, RetrofitCallTypes.RESPONSE_FROM_API)

        Picasso.get().load(R.drawable.splash_screen2).fit().centerCrop().into(iv_splashScreen)


        val handler = Handler()
        handler.postDelayed({
            //FIXME: just for testing more quickly
            //val intent = Intent(this@SplashScreen, MemoryActivity::class.java)
            //startActivity(intent)
            val mainActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }, 2500L)

    }

    private fun <ResponseFromApi> callRetrofit(call: Call<ResponseFromApi>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<ResponseFromApi> {
            override fun onFailure(call: Call<ResponseFromApi>, t: Throwable) {
                Toast.makeText(this@SplashScreen, "fail", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseFromApi>, response: Response<ResponseFromApi>) {
                Log.d(TAG, "Heroku should be awake")
            }
        })
    }
}