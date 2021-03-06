package com.example.lpiem.rickandmortyapp.ViewModel.Connection

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Helpers.PreferencesHelper
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.BackActivity.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Connection.TAG

class SignInManager private constructor(private var context: Context) {

    private lateinit var preferencesHelper: PreferencesHelper
    private var loginAppManager: LoginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var responseFromApiLiveData = MutableLiveData<UserResponse>()
    var loaderLiveData = MutableLiveData<Int>()

    companion object : SingletonHolder<SignInManager, Context>(::SignInManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    private fun regularConnection(email: String, password: String) {
        loaderLiveData.postValue(GONE)
        Toast.makeText(context, context.getString(R.string.account_created), Toast.LENGTH_SHORT).show()
        responseFromApiLiveData = rickAndMortyAPI.regularConnection(email, password)
        responseFromApiLiveData.observeOnce(Observer {
            connectionTreatment(it)
        })
    }

    fun signIn(userName: String, email: String, password: String) {
        responseFromApiLiveData = rickAndMortyAPI.signIn(userName, email, password)
        responseFromApiLiveData.observeOnce(Observer {
            signInTreatment(it, email, password)
        })
    }

    private fun connectionTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val name = userResponse.user?.userName
        val userId = userResponse.user?.userId
        val token = userResponse.user?.sessionToken
        preferencesHelper = PreferencesHelper(context)
        if (token != null) {
            Log.d(TAG, "token added on signIn")
            preferencesHelper.deviceToken = token
        }
        Log.d(TAG, "code = $code body = $userResponse userId = $userId")
        Toast.makeText(context, String.format(context.getString(R.string.welcome), name), Toast.LENGTH_SHORT).show()
        loginAppManager.connectedUser = userResponse.user!!
        val intent = Intent(context, BottomActivity::class.java)
        context.startActivity(intent)
    }

    private fun signInTreatment(userResponse: UserResponse, email: String, password: String) {
        loaderLiveData.postValue(GONE)
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            regularConnection(email, password)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

}