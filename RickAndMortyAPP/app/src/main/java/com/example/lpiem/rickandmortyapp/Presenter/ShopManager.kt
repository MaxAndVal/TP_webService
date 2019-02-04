package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.Wallet
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopManager private constructor(private val context: Context) {

    private val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var currentCall : Call<*>? = null
    private var cost = 0
    private var numberOfDeckToAdd = 0

    companion object : SingletonHolder<ShopManager, Context>(::ShopManager)

    fun cancelCall() {
        currentCall?.cancel()
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    when (type) {
                        BUY_BOOSTER -> {
                            val walletResponse = response.body() as Wallet
                            val code = walletResponse.code
                            val message = walletResponse.message
                            val actualValue = walletResponse.wallet!!
                            if (code == 200) {
                                if (actualValue - cost > 0) {
                                    when(cost) {
                                        40 -> numberOfDeckToAdd = 1
                                        100 -> numberOfDeckToAdd = 3
                                        300 -> numberOfDeckToAdd = 10
                                    }
                                    val body = JsonObject()
                                    body.addProperty("newWallet", actualValue - cost)
                                    currentCall = rickAndMortyAPI!!.updateWallet(loginAppManager.connectedUser!!.userId!!, body)
                                    callRetrofit(currentCall!!, UPDATE_WALLET)
                                } else {
                                    Toast.makeText(context, "Pas assez d'argent pour ce pack", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        UPDATE_WALLET -> {
                            val result = response.body() as ResponseFromApi
                            val code = result.code
                            val message = result.message
                            if (code == 200) {
                                var deckToOpen = result.results!!.deckToOpen!!
                                deckToOpen += numberOfDeckToAdd
                                //Make a call to update the amount of decks to open
                                val jsonObject = JsonObject()
                                jsonObject.addProperty("user_id", loginAppManager.connectedUser!!.userId)
                                jsonObject.addProperty("deckNumber", deckToOpen)
                                currentCall = rickAndMortyAPI!!.increaseNumberOfDecks(jsonObject)
                                callRetrofit(currentCall!!, DECKS_INCREASED)
                            } else {
                                Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        DECKS_INCREASED -> {
                            val result = response.body() as ResponseFromApi
                            val code = result.code
                            val message = result.message
                            if (code == 200) {
                                val user = result.results!!
                                loginAppManager.connectedUser = user
                                Toast.makeText(context, "$numberOfDeckToAdd decks ajoutés à votre pool !", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else -> Log.d(TAG, "error : type does not exist")
                    }

                } else {
                    //TODO : handle API error here
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: " + responseError.string() )
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {

                //TODO : handle error for call here
                Log.d(TAG, "fail : $t")
            }
        })

    }

    fun buyBoosterIfEnable(cost: Int) {
        this.cost = cost
        val user = loginAppManager.connectedUser
        currentCall = rickAndMortyAPI!!.getWallet(user!!.userId!!)
        callRetrofit(currentCall!!, BUY_BOOSTER)
    }

}