package com.example.lpiem.rickandmortyapp.Manager.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce

class ChangePasswordManager internal constructor(internal val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)
    var changePasswordLiveData = MutableLiveData<UserResponse>()
    var isPasswordChangeSucceded = MutableLiveData<Boolean>()


    fun changePassword(oldPass: String, newPass: String) {
        val userEmail = loginAppManager.connectedUser?.userEmail
        val userId = loginAppManager.connectedUser?.userId

        changePasswordLiveData = rickAndMortyAPI.changePassword(userId, userEmail, oldPass, newPass)
        changePasswordLiveData.observeOnce(Observer {
            changePasswordTreatment(it)
        })

    }

    private fun changePasswordTreatment(it: UserResponse?) {
        if (it?.code == SUCCESS) {
            //isPasswordChangeSucceded.postValue(true)
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        } else {
            //isPasswordChangeSucceded.postValue(false)
            Toast.makeText(context, it?.code.toString() + " " + it?.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object : SingletonHolder<ChangePasswordManager, Context>(::ChangePasswordManager)

}