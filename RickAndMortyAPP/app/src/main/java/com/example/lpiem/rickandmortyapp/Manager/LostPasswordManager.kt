package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.JsonProperty
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject

class LostPasswordManager private constructor(private var context: Context) {

    var lostPasswordLiveData = MutableLiveData<UserResponse>()
    var enterCodeLiveData = MutableLiveData<UserResponse>()
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    var isSendCodeSucceded = MutableLiveData<Boolean>()
    var isLoginWithcode = MutableLiveData<Int>()
    private val loginAppManager = LoginAppManager.getInstance(context)


    fun sendCodeManager(user_email: String) {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.UserEmail.string, user_email)
        lostPasswordLiveData = rickAndMortyAPI.sendCode(jsonBody)
        lostPasswordLiveData.observeOnce(Observer {
            lostPasswordTreatment(it)
        })
    }

    private fun lostPasswordTreatment(it: UserResponse?) {
        Log.d(TAG, it?.message)
        if (it?.code == SUCCESS) {
            isSendCodeSucceded.postValue(true)
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        } else {
            isSendCodeSucceded.postValue(false)
            Toast.makeText(context, "Une erreur est survenu, merci de reessayer", Toast.LENGTH_LONG).show()
        }
    }

    fun enterWithCode(code: String) {
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_code", code)
        enterCodeLiveData = rickAndMortyAPI.loginWithCode(jsonBody)
        enterCodeLiveData.observeOnce(Observer {
            loginWithCodeTreatment(it)
        })

    }

    private fun loginWithCodeTreatment(it: ResponseFromApi?) {
        if (it?.code == 200) {
            isLoginWithcode.postValue(it.code)
            loginAppManager.loginTreatment(it)
        } else {
            isLoginWithcode.postValue(it!!.code)
        }
    }


    companion object : SingletonHolder<LostPasswordManager, Context>(::LostPasswordManager)

}