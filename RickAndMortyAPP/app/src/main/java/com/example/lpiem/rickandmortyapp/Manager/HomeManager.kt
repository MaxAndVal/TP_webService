package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.util.Log
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.TAG


class HomeManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)


    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    internal fun updateUserInfo(response: ResponseFromApi){
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
            loginAppManager.connectedUser = response.results!!
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

}