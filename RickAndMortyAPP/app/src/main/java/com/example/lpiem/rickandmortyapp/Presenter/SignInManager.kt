package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.LoginActivity
import com.example.lpiem.rickandmortyapp.View.SignInActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_signin_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInManager private constructor(private var context: Context) {

    private lateinit var intent: Intent
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String

    companion object : SingletonHolder<SignInManager, Context>(::SignInManager)

    init {

    }

    fun regularConnection() {
        Toast.makeText(context, "compte crée", Toast.LENGTH_SHORT).show()
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", (context as SignInActivity).ed_email.text.toString())
        jsonBody.addProperty("user_password", (context as SignInActivity).ed_password.text.toString())
        val connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        callRetrofit(connection, RetrofitCallTypes.CONNECTION)
    }

    fun signIn() {
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_name", (context as SignInActivity).ed_username.text.toString())
        jsonBody.addProperty("user_email", (context as SignInActivity).ed_email.text.toString())
        jsonBody.addProperty("user_password", (context as SignInActivity).ed_password.text.toString())
        val subscribe = rickAndMortyAPI!!.signinUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$subscribe")
        callRetrofit(subscribe, RetrofitCallTypes.SIGN_IN)
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    when (type) {
                        RetrofitCallTypes.SIGN_IN -> {
                            val responseFromApi = response.body() as ResponseFromApi
                            val code = responseFromApi.code
                            val message = responseFromApi.message
                            if (code == 200) {
                                regularConnection()
                            } else {
                                Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RetrofitCallTypes.CONNECTION -> {
                            val responseFromApi = response.body() as ResponseFromApi
                            val code = responseFromApi.code
                            val results = responseFromApi.results?.userName
                            val userId = responseFromApi.results?.userId
                            Log.d(TAG, "body = ${response.body()}")
                            Toast.makeText(context, "code : $code, bienvenue $results id:$userId", Toast.LENGTH_SHORT).show()
                            intent = Intent(context, BottomActivity::class.java)
                            intent.putExtra("user", responseFromApi.results)
                            context.startActivity(intent)
                        }
                        else -> Log.d(TAG, "error : ${response.errorBody()}")
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

    fun goBack() {
        val mainIntent = Intent(context, LoginActivity::class.java)
        context.startActivity(mainIntent)
    }
}