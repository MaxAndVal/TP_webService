package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.Wallet
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.google.gson.JsonObject
import retrofit2.Call

class ShopManager private constructor(private val context: Context) {

    private val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var currentCall : Call<*>? = null
    private var cost = 0
    private var numberOfDeckToAdd = 0
    private var responseFromApiLiveData = MutableLiveData<ResponseFromApi>()
    private var walletLiveData = MutableLiveData<Wallet>()

    companion object : SingletonHolder<ShopManager, Context>(::ShopManager)

    fun cancelCall() {
        currentCall?.cancel()
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
            responseFromApiLiveData = rickAndMortyAPI.increaseDeckNumber(jsonObject)
            responseFromApiLiveData.observeOnce(Observer {
                decksIncreasedTreatment(it)
            })

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
                val jsonObject = JsonObject()
                jsonObject.addProperty("newWallet", actualValue - cost)
                val userId = loginAppManager.connectedUser!!.userId!!
                responseFromApiLiveData = rickAndMortyAPI.updateWalletValue(jsonObject, userId)
                responseFromApiLiveData.observeOnce(Observer {
                    updateWalletTreatment(it)
                })
            } else {
                Toast.makeText(context, "Pas assez d'argent pour ce pack", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
        }
    }

    fun buyBoosterIfEnable(cost: Int) {
        this.cost = cost
        val userId = loginAppManager.connectedUser!!.userId!!
        walletLiveData = rickAndMortyAPI.buyBooster(userId)
        walletLiveData.observeOnce(Observer {
            buyBoosterTreatment(it)
        })
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