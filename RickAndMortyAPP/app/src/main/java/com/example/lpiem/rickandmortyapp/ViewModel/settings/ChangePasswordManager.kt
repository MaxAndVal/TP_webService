package com.example.lpiem.rickandmortyapp.ViewModel.settings

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager

class ChangePasswordManager internal constructor(internal val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)
    var changePasswordLiveData = MutableLiveData<UserResponse>()
    var isPasswordChangeSucceeded = MutableLiveData<Boolean>()
    var closeFragPassLiveData = MutableLiveData<Fragment>()

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

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
            isPasswordChangeSucceeded.postValue(true)
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        } else {
            isPasswordChangeSucceeded.postValue(false)
            Toast.makeText(context, it?.code.toString() + " " + it?.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object : SingletonHolder<ChangePasswordManager, Context>(::ChangePasswordManager)

}