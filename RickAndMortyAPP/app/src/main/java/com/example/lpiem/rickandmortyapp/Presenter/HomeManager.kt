package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.util.Pair
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.KaamlottQuote
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var citation = ""
    private var personnage = ""
    private var fragment : HomeFragment?=null

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    init {
    }

    @Synchronized
    fun getRandomQuote() {
        val resultCall = rickAndMortyAPI!!.getRamdomQuote()
        RickAndMortyRetrofitSingleton.callRetrofit(resultCall, RetrofitCallTypes.KAAMELOTT_QUOTE, context, fragment!!)
    }

    fun getFragment(fragment: HomeFragment){
        this.fragment = fragment
    }


}