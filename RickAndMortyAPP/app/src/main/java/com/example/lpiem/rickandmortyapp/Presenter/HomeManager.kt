package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.util.Pair
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
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

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    init {

    }

    @Synchronized
    fun getRandomQuote(): Pair<String, String> {
        val resultCall = rickAndMortyAPI!!.getRamdomQuote()
        callRetrofit(resultCall, RetrofitCallTypes.KAAMELOTT_QUOTE)
        val list = Pair(citation, personnage)
        return list
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.KAAMELOTT_QUOTE -> {
                            var kaamlott = response.body() as KaamlottQuote
                            var code = kaamlott.code
                            if (kaamlott.code == 200) {

                                citation = kaamlott.citation!!
                                personnage = kaamlott.personnage!!

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