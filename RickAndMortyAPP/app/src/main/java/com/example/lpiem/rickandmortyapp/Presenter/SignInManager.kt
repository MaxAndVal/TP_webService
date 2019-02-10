package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.JsonProperty.*
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.SignInActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_signin.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInManager private constructor(private var context: Context) {

    private var loginAppManager = LoginAppManager.getInstance(context)

    companion object : SingletonHolder<SignInManager, Context>(::SignInManager)

    init {

    }

    private fun regularConnection() {
        (context as SignInActivity).progress_bar_sign_in.visibility = GONE
        Toast.makeText(context, "compte crée", Toast.LENGTH_SHORT).show()
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty(UserEmail.string, (context as SignInActivity).ed_email.text.toString())
        jsonBody.addProperty(UserPassword.string, (context as SignInActivity).ed_password.text.toString())
        val connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        callRetrofit(connection, RetrofitCallTypes.CONNECTION)
    }

    fun signIn() {
        (context as SignInActivity).progress_bar_sign_in.visibility = VISIBLE
        val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        if ((context as SignInActivity).ed_email.text.toString() == "" || (context as SignInActivity).ed_password.text.toString() == "" || (context as SignInActivity).ed_username.text.toString() == "") {
            Toast.makeText(context, context.getString(R.string.thanks_to_fill_all_fields), Toast.LENGTH_SHORT).show()
            (context as SignInActivity).progress_bar_sign_in.visibility = GONE
        } else {
            val jsonBody = JsonObject()
            jsonBody.addProperty(UserName.string, (context as SignInActivity).ed_username.text.toString())
            jsonBody.addProperty(UserEmail.string, (context as SignInActivity).ed_email.text.toString())
            jsonBody.addProperty(UserPassword.string, (context as SignInActivity).ed_password.text.toString())
            val subscribe = rickAndMortyAPI!!.signInUser(jsonBody)
            Log.d(TAG, "jsonBody : $jsonBody")
            Log.d(TAG, "$subscribe")
            callRetrofit(subscribe, RetrofitCallTypes.SIGN_IN)
        }
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.SIGN_IN -> {
                            signInTreatment(result as ResponseFromApi)
                        }
                        RetrofitCallTypes.CONNECTION -> {
                            connectionTreatment(result as ResponseFromApi)
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

    private fun connectionTreatment(response: ResponseFromApi) {
        val code = response.code
        val name = response.results?.userName
        val userId = response.results?.userId
        Log.d(TAG, "body = $response")
        Toast.makeText(context, "code : $code, bienvenue $name id: $userId", Toast.LENGTH_SHORT).show()
        loginAppManager.connectedUser = response.results!!
        val intent = Intent(context, BottomActivity::class.java)
        context.startActivity(intent)
    }

    private fun signInTreatment(response: ResponseFromApi) {
        (context as SignInActivity).progress_bar_sign_in.visibility = GONE
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            regularConnection()
        } else {
            Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
        }
    }

}