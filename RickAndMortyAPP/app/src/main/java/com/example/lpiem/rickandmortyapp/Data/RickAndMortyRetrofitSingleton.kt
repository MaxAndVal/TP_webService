package com.example.lpiem.rickandmortyapp.Data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lpiem.rickandmortyapp.Model.KaamlottQuote
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RickAndMortyRetrofitSingleton {

    //private const val BASE_URL = "https://rickandmortyapi.com/"
    private const val BASE_URL = "https://api-rickandmorty-tcg.herokuapp.com"

    private var rickAndMortyAPIInstance: RickAndMortyAPI? = null

    val instance: RickAndMortyAPI?
        get() {
            if (rickAndMortyAPIInstance == null)
                synchronized(RickAndMortyAPI::class.java) {
                    createApiBuilder()
                }
            return rickAndMortyAPIInstance
        }

    private fun createApiBuilder() {
        val gsonInstance = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonInstance))
                .build()
        rickAndMortyAPIInstance = retrofit.create(RickAndMortyAPI::class.java)
    }
    fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes, context: Context, fragment:Fragment) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.KAAMELOTT_QUOTE -> {
                            var kaamlott = response.body() as KaamlottQuote
                            var code = kaamlott.code
                            if (kaamlott.code == 200) {

                                var citation = kaamlott.citation!!
                                var personnage = kaamlott.personnage!!
                                fragment.tv_citation.text = citation
                                fragment.tv_auteur.text = personnage


                            } else {
                                Toast.makeText(context, "code : $code, message ${kaamlott.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
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
}}
