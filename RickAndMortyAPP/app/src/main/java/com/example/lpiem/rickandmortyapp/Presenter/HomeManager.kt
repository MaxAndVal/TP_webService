package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.JsonProperty.NewDate
import com.example.lpiem.rickandmortyapp.Data.JsonProperty.NewWallet
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.KaamlottQuote
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Model.Wallet
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Home.HomeDisplayUI
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

const val SUCCESS = 200

class HomeManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var citation = ""
    private var personnage = ""
    private var personnageNameList = listOf("")
    internal var score = 0
    internal var turn = 0
    private lateinit var currentCall: Call<*>
    private lateinit var homeDisplayUI: HomeDisplayUI
    private val loginAppManager = LoginAppManager.getInstance(context)

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    fun cancelCall() {
        currentCall.cancel()
        Log.d(TAG, "call canceled !!")
    }

    fun getRandomQuote(link: HomeDisplayUI) {
        homeDisplayUI = link
        val resultCall = rickAndMortyAPI!!.getRandomQuote()
        callRetrofit(resultCall, KAAMELOTT_QUOTE)
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    val result = response.body()
                    when (type) {
                        KAAMELOTT_QUOTE -> {
                            getKaamelottQuoteTreatment(result as KaamlottQuote)
                        }
                        GET_USER_BY_ID -> {
                            getUserByIdTreatment(result as ResponseFromApi)
                        }
                        PUT_DATE -> {
                            putDateTreatment(result as ResponseFromApi)
                        }
                        UPDATE_WALLET -> {
                            updateUserWalletTreatment(result as ResponseFromApi)
                        }
                        GET_WALLET -> {
                            getWalletTreatment(result as Wallet)
                        }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

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

    private fun updateUserWalletTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
            val id = loginAppManager.connectedUser!!.userId
            currentCall = rickAndMortyAPI!!.getWallet(id!!)
            callRetrofit(currentCall, GET_WALLET)
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
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

    private fun getKaamelottQuoteTreatment(response: KaamlottQuote) {
        val code = response.code
        val message = response.message
        if (response.code == SUCCESS) {
            citation = response.citation!!
            personnage = response.personnage!!
            personnageNameList = response.personnageList!!
            val list = Triple(citation, personnage, personnageNameList)
            homeDisplayUI.updateUI(list)
        } else {
            Log.d(TAG, "code : $code, message $message")
        }
    }

    fun gameAvailable(user: User, link: HomeDisplayUI) {
        homeDisplayUI = link
        currentCall = rickAndMortyAPI!!.getUserById(user.userId!!)
        callRetrofit(currentCall, RetrofitCallTypes.GET_USER_BY_ID)
    }

    fun putDateToken() {
        val jsonBody = JsonObject()
        jsonBody.addProperty(NewDate.string, getDate())
        currentCall = rickAndMortyAPI!!.putNewDate(loginAppManager.connectedUser!!.userId!!, jsonBody)
        callRetrofit(currentCall, PUT_DATE)
    }

    private fun getDate(): String {
        var day = Calendar.getInstance(Locale.FRENCH).get(Calendar.DAY_OF_YEAR).toString()
        var month = (Calendar.getInstance(Locale.FRENCH).get(Calendar.MONTH) + 1).toString()
        val year = Calendar.getInstance(Locale.FRENCH).get(Calendar.YEAR).toString()
        if (day.length == 1) day = "0$day"
        if (month.length == 1) month = "0$month"
        val dateToReturn = day + month + year
        Log.d(TAG, "date = $dateToReturn")
        //Tue Jan 29 14:05:09 GMT+01:00 2019 //-> 29012019
        return dateToReturn
    }

    fun updatePickleRick(score: Int) {
        val user = loginAppManager.connectedUser
        val jsonBody = JsonObject()
        jsonBody.addProperty(NewWallet.string, (user!!.userWallet!! + (score * 10)))
        currentCall = rickAndMortyAPI!!.updateWallet(user.userId!!, jsonBody)
        callRetrofit(currentCall, UPDATE_WALLET)
    }
}