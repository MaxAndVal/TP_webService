package com.example.lpiem.rickandmortyapp.ViewModel.Home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.Games.KaamelottQuizBundle
import com.example.lpiem.rickandmortyapp.Model.Games.KaamlottQuote
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Wallet
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import java.text.SimpleDateFormat
import java.util.*

class KaamelottManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    internal var score = 0
    internal var turn = 0
    private val loginAppManager = LoginAppManager.getInstance(context)
    private var kaamelottLiveData = MutableLiveData<KaamlottQuote>()
    private var responseFromApiLiveData = MutableLiveData<UserResponse>()
    private var walletLiveData = MutableLiveData<Wallet>()
    var initDisplayContent = MutableLiveData<Unit>()
    var updateUI = MutableLiveData<KaamelottQuizBundle>()

    companion object : SingletonHolder<KaamelottManager, Context>(::KaamelottManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    fun getRandomQuote() {
        kaamelottLiveData = rickAndMortyAPI.getRandomQuote()
        kaamelottLiveData.observeOnce(Observer {
            getKaamelottQuoteTreatment(it)
        })
    }

    private fun getKaamelottQuoteTreatment(response: KaamlottQuote) {
        val code = response.code
        val message = response.message
        if (response.code == SUCCESS) {
            val citation = response.citation!!
            val personage = response.personnage!!
            val personageNameList = response.personnageList!!
            val shuffleList = listOf(personage, personageNameList[0], personageNameList[1], personageNameList[2]).shuffled()
            val bundle = KaamelottQuizBundle(citation, personage, shuffleList)
            updateUI.postValue(bundle)
        } else {
            Log.d(TAG, "code : $code, message $message")
        }
    }

    fun gameAvailable(user: User) {
        responseFromApiLiveData = rickAndMortyAPI.getUserById(user.userId)
        responseFromApiLiveData.observeOnce(Observer {
            getUserByIdTreatment(it)
        })
    }

    private fun getUserByIdTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            val user = userResponse.user
            loginAppManager.gameInProgress = getDate() != user?.userLastGame
            initDisplayContent.postValue(Unit)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message_userNotFound), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    fun putDateToken() {
        val id = loginAppManager.connectedUser!!.userId
        responseFromApiLiveData = rickAndMortyAPI.putDateToken(getDate(),id)
        responseFromApiLiveData.observeOnce(Observer {
            putDateTreatment(it)
        })
    }

    private fun putDateTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        return formatter.format(date)
    }

    fun updatePickleRick(score: Int) {
        val user = loginAppManager.connectedUser
        responseFromApiLiveData = rickAndMortyAPI.updateWallet(score, user)
        responseFromApiLiveData.observeOnce(Observer {
            updateUserWalletTreatment(it)
        })
    }

    private fun updateUserWalletTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
            val id = loginAppManager.connectedUser!!.userId
            walletLiveData = rickAndMortyAPI.getWallet(id)
            walletLiveData.observeOnce(Observer {
                getWalletTreatment(it)
            })
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

    private fun getWalletTreatment(response: Wallet) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val wallet = response.wallet
            Log.d(TAG, "success code : $code, message $message, wallet $wallet")
            loginAppManager.connectedUser?.userWallet = wallet!!
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }
}