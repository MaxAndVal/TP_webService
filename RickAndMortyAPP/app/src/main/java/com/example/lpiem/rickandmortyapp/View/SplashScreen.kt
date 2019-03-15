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

        tokenConnectionObserver = Observer {
            Log.d(TAG, " log 3")
            if (it.code == SUCCESS) {
                loginAppManager.loginTreatment(it, LoginFrom.FROM_SPLASH_SCREEN)
                finish()
            } else {
                Log.d(TAG, "log5")
//                val loginActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
//                startActivity(loginActivityIntent)
//                finish()
            }
        }

        preferencesHelper = PreferencesHelper(this)
        preferencesHelper.deviceToken = "B9L6YQgLjDRIO3WNvYvXGFnG9GsOHr"

        loginAppManager = LoginAppManager.getInstance(this)
        //loginAppManager.loginLiveData.observeOnce(tokenConnectionObserver)

        val resultCall = rickAndMortyAPI!!.herokuAwaking()
        callRetrofit(resultCall, RetrofitCallTypes.HEROKU_VOID)

        Picasso.get().load(R.drawable.splash_screen2).fit().centerCrop().into(iv_splashScreen)


//        val handler = Handler()
//        handler.postDelayed({
//            val mainActivityIntent = Intent(this@SplashScreen, LoginActivity::class.java)
//            startActivity(mainActivityIntent)
//            finish()
//        }, 2500L)

    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "F : Heroku should be awake" + t.message.toString())
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                when (type) {
                    HEROKU_VOID -> {
                        Log.d(TAG, " R : Heroku should be awake")
                        if (preferencesHelper.deviceToken != "") {
                            Log.d(TAG, " log 1")
                            loginAppManager.connectionWithToken(preferencesHelper.deviceToken, tokenConnectionObserver)
//                            val bottomActivityIntent = Intent(this@SplashScreen, BottomActivity::class.java)
//                            startActivity(bottomActivityIntent)
//                            finish()
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