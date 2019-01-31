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
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_bottom.*
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
    private var fragment : HomeFragment?=null

    companion object : SingletonHolder<HomeManager, Context>(::HomeManager)

    init {

    }

    fun getRandomQuote() /*: Triple<String, String, List<String>>*/ {
        val resultCall = rickAndMortyAPI!!.getRamdomQuote()
        callRetrofit(resultCall, KAAMELOTT_QUOTE)
        //TODO: migrate like that or not
        //RickAndMortyRetrofitSingleton.callRetrofit(resultCall, RetrofitCallTypes.KAAMELOTT_QUOTE, context, fragment!!)
        //return Triple(citation, personnage, personnageNameList)
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        KAAMELOTT_QUOTE -> {
                            val kaamlott = response.body() as KaamlottQuote
                            val code = kaamlott.code
                            if (kaamlott.code == SUCCESS) {
                                citation = kaamlott.citation!!
                                personnage = kaamlott.personnage!!
                                personnageNameList = kaamlott.personnageList!!
                                val list = Triple(citation, personnage, personnageNameList)
                                fragment?.updateUI(list)
                            } else {
                                Log.d(TAG, "code : $code, message ${kaamlott.message}")
                            }
                        }
                        GET_USER_BY_ID -> {
                            val responseFromApi = response.body() as ResponseFromApi
                            val loginAppManager = LoginAppManager.getInstance(context)
                            if (responseFromApi.code == SUCCESS) {
                                loginAppManager.gameInProgress = getDate() != responseFromApi.results?.userLastGame
                                fragment?.displayFragmentContent()
                            } else {
                                Toast.makeText(context, "User not found, error : ${responseFromApi.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        PUT_DATE -> {
                            val newDateResponse = response.body() as ResponseFromApi
                            if (newDateResponse.code == SUCCESS) {
                                Log.d(TAG,"success code : ${newDateResponse.code}, message ${newDateResponse.message}")
                            } else {
                                Log.d(TAG,"error code : ${newDateResponse.code}, message ${newDateResponse.message}")
                            }
                        }
                        UPDATE_WALLET -> {
                            val walletUpdateResponse = response.body() as ResponseFromApi
                            if (walletUpdateResponse.code == SUCCESS) {
                                Log.d(TAG,"success code : ${walletUpdateResponse.code}, message ${walletUpdateResponse.message}")
                                val id = LoginAppManager.getInstance(context).connectedUser!!.userId
                                val getWallet = rickAndMortyAPI!!.getWallet(id!!)
                                callRetrofit(getWallet, GET_WALLET)
                            } else {
                                Log.d(TAG,"error code : ${walletUpdateResponse.code}, message ${walletUpdateResponse.message}")
                            }
                        }
                        GET_WALLET -> {
                            val newWalletResponse = response.body() as Wallet
                            if (newWalletResponse.code == SUCCESS) {
                                Log.d(TAG,"success code : ${newWalletResponse.code}, message ${newWalletResponse.message}, wallet ${newWalletResponse.wallet}")
                                (context as BottomActivity).tv_wallet.text = String.format(context.getString(R.string.wallet_amount), newWalletResponse.wallet, " ")
                            } else {
                                Log.d(TAG,"error code : ${newWalletResponse.code}, message ${newWalletResponse.message}")
                            }
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

    fun getFragment(fragment: HomeFragment){
        this.fragment = fragment
    }

    fun gameAvailable(user: User) {
        val getUser = rickAndMortyAPI!!.getUserById(user.userId!!)
        callRetrofit(getUser, RetrofitCallTypes.GET_USER_BY_ID)
    }

    fun putDateToken() {
        val jsonBody = JsonObject()
        jsonBody.addProperty(NewDate.string, getDate())
        val putDate = rickAndMortyAPI!!.putNewDate(LoginAppManager.getInstance(context).connectedUser!!.userId!!, jsonBody)
        callRetrofit(putDate, PUT_DATE)
    }

    fun getDate(): String {
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
        val user = LoginAppManager.getInstance(context).connectedUser
        val jsonBody = JsonObject()
        jsonBody.addProperty(NewWallet.string, (user!!.userWallet!! + (score * 10)))
        val newWallet = rickAndMortyAPI!!.updateWallet(user.userId!!, jsonBody)
        callRetrofit(newWallet, UPDATE_WALLET)
    }
}