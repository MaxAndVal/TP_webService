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
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Home.HomeDisplayUI
import com.example.lpiem.rickandmortyapp.View.TAG
import java.text.SimpleDateFormat
import java.util.*


class HomeManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)

    private var citation = ""
    private var personnage = ""
    private var personnageNameList = listOf("")
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
            citation = response.citation!!
            personnage = response.personnage!!
            personnageNameList = response.personnageList!!
            //TODO: shuffle here
            val shuffleList = listOf(personnage, personnageNameList[0], personnageNameList[1], personnageNameList[2]).shuffled()
            //val list = Triple(citation, personnage, personnageNameList)
            homeDisplayUI.updateUI(citation, personnage, shuffleList)
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
            Toast.makeText(context, "code $code User not found, error : $message", Toast.LENGTH_SHORT).show()
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
        //TODO : test
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())

//        var day = Calendar.getInstance(Locale.US).get(Calendar.DAY_OF_YEAR).toString()
//        var month = (Calendar.getInstance(Locale.US).get(Calendar.MONTH) + 1).toString()
//        val year = Calendar.getInstance(Locale.US).get(Calendar.YEAR).toString()
//        if (day.length == 1) day = "0$day"
//        if (month.length == 1) month = "0$month"
//        val dateToReturn = day + month + year
        Log.d(TAG, "date = $date")
        Log.d(TAG,"date formatted : ${formatter.format(date)}")
        //Tue Jan 29 14:05:09 GMT+01:00 2019 //-> 29012019
        return formatter.format(date)//dateToReturn
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