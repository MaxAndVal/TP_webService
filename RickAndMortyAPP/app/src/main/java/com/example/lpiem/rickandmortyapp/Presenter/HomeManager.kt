package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.KAAMELOTT_QUOTE
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.KaamlottQuote
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var citation = ""
    private var personnage = ""
    private var personnageNameList = listOf("")

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    init {

    }

    //@Synchronized
    fun getRandomQuote(): Triple<String, String, List<String>> {
        val resultCall = rickAndMortyAPI!!.getRamdomQuote()
        callRetrofit(resultCall, KAAMELOTT_QUOTE)
        return Triple(citation, personnage, personnageNameList)
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        KAAMELOTT_QUOTE -> {
                            val kaamlott = response.body() as KaamlottQuote
                            val code = kaamlott.code
                            if (kaamlott.code == 200) {
                                citation = kaamlott.citation!!
                                personnage = kaamlott.personnage!!
                                personnageNameList = kaamlott.personnageList!!
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

    }
}