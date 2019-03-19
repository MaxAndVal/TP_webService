package com.example.lpiem.rickandmortyapp.ViewModel.Home

import android.content.Context
import android.util.Log
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Connection.TAG


class HomeManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)


    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    internal fun updateUserInfo(userResponse: UserResponse){
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
            loginAppManager.connectedUser = userResponse.user!!
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

}