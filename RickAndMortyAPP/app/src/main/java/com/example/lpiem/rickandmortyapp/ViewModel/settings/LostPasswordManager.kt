package com.example.lpiem.rickandmortyapp.ViewModel.settings

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Helpers.JsonProperty.UserCode
import com.example.lpiem.rickandmortyapp.Data.Helpers.JsonProperty.UserEmail
import com.example.lpiem.rickandmortyapp.Data.Helpers.LoginFrom
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.google.gson.JsonObject

class LostPasswordManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)
    var lostPasswordLiveData = MutableLiveData<UserResponse>()
    var enterCodeLiveData = MutableLiveData<UserResponse>()
    var isSendCodeSucceeded = MutableLiveData<Boolean>()
    var isLoginWithCode = MutableLiveData<Int>()

    companion object : SingletonHolder<LostPasswordManager, Context>(::LostPasswordManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    fun sendCodeManager(userEmail: String) {
        val jsonBody = JsonObject()
        jsonBody.addProperty(UserEmail.dbField, userEmail)
        lostPasswordLiveData = rickAndMortyAPI.sendCode(jsonBody)
        lostPasswordLiveData.observeOnce(Observer {
            lostPasswordTreatment(it)
        })
    }

    private fun lostPasswordTreatment(it: UserResponse?) {
        Log.d(TAG, it?.message)
        if (it?.code == SUCCESS) {
            isSendCodeSucceeded.postValue(true)
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        } else {
            isSendCodeSucceeded.postValue(false)
            Toast.makeText(context, "Une erreur est survenu, merci de reessayer", Toast.LENGTH_LONG).show()
        }
    }

    fun enterWithCode(code: String) {
        val jsonBody = JsonObject()
        jsonBody.addProperty(UserCode.dbField, code)
        enterCodeLiveData = rickAndMortyAPI.loginWithCode(jsonBody)
        enterCodeLiveData.observeForever(Observer {
            loginWithCodeTreatment(it)
        })
        enterCodeLiveData = rickAndMortyAPI.loginWithCode(jsonBody)
    }

    private fun loginWithCodeTreatment(it: UserResponse?) {
        if (it?.code == 200) {
            Log.d("test", "traitement")
            isLoginWithCode.postValue(it.code)
            loginAppManager.loginTreatment(it, LoginFrom.FROM_LOST_PASSWORD)
        } else {
            Log.d("test", "traitement else")
            isLoginWithCode.postValue(it!!.code)
        }
    }

}