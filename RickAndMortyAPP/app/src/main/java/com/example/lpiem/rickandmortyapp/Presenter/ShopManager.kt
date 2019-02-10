package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.Wallet
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
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
                    val result = response.body()
                    when (type) {
                        BUY_BOOSTER -> {
                            buyBoosterTreatment(result as Wallet)
                        }
                        UPDATE_WALLET -> {
                            updateWalletTreatment(result as ResponseFromApi)
                        }
                        DECKS_INCREASED -> {
                            decksIncreasedTreatment(result as ResponseFromApi)
                        }
                        else -> Log.d(TAG, "error : type does not exist")
                    }

                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: " + responseError.string() )
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    private fun decksIncreasedTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val user = response.results!!
            loginAppManager.connectedUser = user
            Toast.makeText(context, "$numberOfDeckToAdd decks ajoutés à votre pool !", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateWalletTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            var deckToOpen = response.results!!.deckToOpen!!
            deckToOpen += numberOfDeckToAdd
            // Make a call to update the amount of decks to open
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", loginAppManager.connectedUser!!.userId)
            jsonObject.addProperty("deckNumber", deckToOpen)
            currentCall = rickAndMortyAPI!!.increaseNumberOfDecks(jsonObject)
            callRetrofit(currentCall!!, DECKS_INCREASED)
        } else {
            Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buyBoosterTreatment(walletResponse: Wallet) {
        val code = walletResponse.code
        val message = walletResponse.message
        if (code == SUCCESS) {
            val actualValue = walletResponse.wallet!!
            if (actualValue - cost >= 0) {
                numberOfDeckToAdd = howManyDecksToAdd(cost)
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

    fun buyBoosterIfEnable(cost: Int) {
        this.cost = cost
        val user = loginAppManager.connectedUser
        currentCall = rickAndMortyAPI!!.getWallet(user!!.userId!!)
        callRetrofit(currentCall!!, BUY_BOOSTER)
    }

    private fun howManyDecksToAdd(cost: Int) : Int {
        return when (cost) {
            40 ->  1
            100 -> 3
            300 -> 10
            else -> 0
        }
    }

}