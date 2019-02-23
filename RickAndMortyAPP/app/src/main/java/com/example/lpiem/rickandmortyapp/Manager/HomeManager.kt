package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.KaamlottQuote
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Model.Wallet
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Home.HomeDisplayUI
import com.example.lpiem.rickandmortyapp.View.TAG
import java.text.SimpleDateFormat
import java.util.*


class HomeManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    internal var score = 0
    internal var turn = 0
    private lateinit var homeDisplayUI: HomeDisplayUI
    private val loginAppManager = LoginAppManager.getInstance(context)
    private var kaamelottLiveData = MutableLiveData<KaamlottQuote>()
    private var responseFromApiLiveData = MutableLiveData<ResponseFromApi>()
    private var walletLiveData = MutableLiveData<Wallet>()

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    fun getRandomQuote(link: HomeDisplayUI) {
        homeDisplayUI = link

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
            homeDisplayUI.updateUI(citation, personage, shuffleList)
        } else {
            Log.d(TAG, "code : $code, message $message")
        }
    }

    fun gameAvailable(user: User, link: HomeDisplayUI) {
        homeDisplayUI = link
        responseFromApiLiveData = rickAndMortyAPI.getUserById(user.userId)
        responseFromApiLiveData.observeOnce(Observer {
            getUserByIdTreatment(it)
        })
    }

    private fun getUserByIdTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val results = response.results
            loginAppManager.gameInProgress = getDate() != results?.userLastGame
            homeDisplayUI.displayFragmentContent()
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

    private fun putDateTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        Log.d(TAG, "date = $date")
        Log.d(TAG,"date formatted : ${formatter.format(date)}")
        return formatter.format(date)
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

    fun updatePickleRick(score: Int) {
        val user = loginAppManager.connectedUser
        responseFromApiLiveData = rickAndMortyAPI.updateWallet(score, user)
        responseFromApiLiveData.observeOnce(Observer {
            updateUserWalletTreatment(it)
        })
    }

    private fun updateUserWalletTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
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
            homeDisplayUI.updatePickleRicksAmount(wallet!!, " ")
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }
}