package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.*
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.HEROKU_VOID
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.UserResponse
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashScreen : AppCompatActivity() {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(this).instance
    private lateinit var loginAppManager: LoginAppManager
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var tokenConnectionObserver: Observer<UserResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferencesHelper = PreferencesHelper(this)
        Log.d(TAG, "existing token = ${preferencesHelper.deviceToken}")

        tokenConnectionObserver = Observer {
            Log.d(TAG, " log 3")
            if (it.code == SUCCESS) {
                loginAppManager.loginTreatment(it, LoginFrom.FROM_SPLASH_SCREEN)
                finish()
            } else {
                val loginActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(loginActivityIntent)
                finish()
            }
        }

        loginAppManager = LoginAppManager.getInstance(this)

        val resultCall = rickAndMortyAPI!!.herokuAwaking()
        callRetrofit(resultCall, RetrofitCallTypes.HEROKU_VOID)

        Picasso.get().load(R.drawable.splash_screen2).fit().centerCrop().into(iv_splashScreen)

    }

    //TODO : move to repo
    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "F : Heroku should be awake" + t.message.toString())
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                when (type) {
                    HEROKU_VOID -> {
                        Log.d(TAG, " R : Heroku should be awake")
                        if (preferencesHelper.deviceToken.length == 30) {
                            Log.d(TAG, " log 1")
                            loginAppManager.connectionWithToken(preferencesHelper.deviceToken, tokenConnectionObserver)
                        } else {
                            val loginActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
                            startActivity(loginActivityIntent)
                            finish()
                        }
                    }
                    else -> {
                        Log.d(TAG, "Nothing have to pass by here ^^")
                    }
                }

            }
        })
    }
}