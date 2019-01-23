package com.example.lpiem.rickandmortyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_signin_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity(), Callback<ResponseFromApi> {
    override fun onFailure(call: Call<ResponseFromApi>, t: Throwable) {
        Log.d(TAG, "failure : $t")
    }

    override fun onResponse(call: Call<ResponseFromApi>, response: Response<ResponseFromApi>) {
        if (response.isSuccessful) {
            var code = response.body()?.code
            var message = response.body()?.message
            if (code == 200) {
                regularConnection();
            } else {
                Toast.makeText(this, "code : $code, message : ${message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "error : ${response.errorBody()}")
        }
    }

    private fun regularConnection() {
        Toast.makeText(this, "compte crée", Toast.LENGTH_SHORT).show()
        var email = "${ed_email.text}"
        var password = "${ed_password.text}"
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", email)
        jsonBody.addProperty("user_password", password)
        var connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        callRetrofit(connection, 2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_activity)

        tv_alreadyAccount.setOnClickListener { goBack() }
        btn_confSignIn.setOnClickListener { signin() }

    }

    private fun signin() {
        var username = "${ed_username.text}"
        var email = "${ed_email.text}"
        var password = "${ed_password.text}"
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_name", username)
        jsonBody.addProperty("user_email", email)
        jsonBody.addProperty("user_password", password)
        var subscribe = rickAndMortyAPI!!.signinUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$subscribe")
        callRetrofit(subscribe, 1)
    }

    private fun <T> callRetrofit(call: Call<T>, i: Int) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if (i == 1) {
                        val responseFromApi = response.body() as ResponseFromApi
                        var code = responseFromApi.code
                        var message = responseFromApi.message
                        if (code == 200) {
                            regularConnection();
                        } else {
                            Toast.makeText(applicationContext, "code : $code, message : ${message}", Toast.LENGTH_SHORT).show()
                        }
                    } else if (i == 2) {
                        val responseFromApi = response.body() as ResponseFromApi
                        val code = responseFromApi.code
                        val results = responseFromApi.results?.userName
                        val user_id = responseFromApi.results?.userId
                        Log.d(TAG, "body = ${response.body()}")
                        Toast.makeText(applicationContext, "code : $code, bienvenue $results id:$user_id", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this@SigninActivity, BottomActivity::class.java)
                        intent.putExtra("user", responseFromApi.results)
                        startActivity(intent)
                    } else {
                        Log.d(TAG, "error : ${response.errorBody()}")
                    }

                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    private fun goBack() {
        var mainIntent = Intent(this@SigninActivity, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
