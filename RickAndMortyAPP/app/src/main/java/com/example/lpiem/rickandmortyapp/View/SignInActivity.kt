package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.R
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_signin_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    private fun regularConnection() {
        Toast.makeText(this, "compte crée", Toast.LENGTH_SHORT).show()
        val email = "${ed_email.text}"
        val password = "${ed_password.text}"
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", email)
        jsonBody.addProperty("user_password", password)
        val connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        callRetrofit(connection, 2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_activity)

        tv_alreadyAccount.setOnClickListener { goBack() }
        btn_confSignIn.setOnClickListener { signIn() }

    }

    private fun signIn() {
        val username = "${ed_username.text}"
        val email = "${ed_email.text}"
        val password = "${ed_password.text}"
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_name", username)
        jsonBody.addProperty("user_email", email)
        jsonBody.addProperty("user_password", password)
        val subscribe = rickAndMortyAPI!!.signinUser(jsonBody)
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
                        val code = responseFromApi.code
                        val message = responseFromApi.message
                        if (code == 200) {
                            regularConnection()
                        } else {
                            Toast.makeText(applicationContext, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                        }
                    } else if (i == 2) {
                        val responseFromApi = response.body() as ResponseFromApi
                        val code = responseFromApi.code
                        val results = responseFromApi.results?.userName
                        val userId = responseFromApi.results?.userId
                        Log.d(TAG, "body = ${response.body()}")
                        Toast.makeText(applicationContext, "code : $code, bienvenue $results id:$userId", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignInActivity, BottomActivity::class.java)
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
        val mainIntent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
