package com.example.lpiem.rickandmortyapp.ViewModel.BackActivity

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Helpers.JsonProperty.*
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.CardBooster
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Wallet
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.google.gson.JsonObject

class ShopManager private constructor(private val context: Context) {

    private val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var cost = 0
    private var numberOfDeckToAdd = 0
    private var responseFromApiLiveData = MutableLiveData<UserResponse>()
    private var walletLiveData = MutableLiveData<Wallet>()
    var transactionInProgress = false

    companion object : SingletonHolder<ShopManager, Context>(::ShopManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    private fun decksIncreasedTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            val user = userResponse.user!!
            loginAppManager.connectedUser = user
            Toast.makeText(context, String.format(context.getString(R.string.X_decks_added), numberOfDeckToAdd), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
        transactionInProgress = false
    }

    private fun updateWalletTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            var deckToOpen = userResponse.user!!.deckToOpen!!
            deckToOpen += numberOfDeckToAdd
            // Make a call to update the amount of decks to open
            val jsonObject = JsonObject()
            jsonObject.addProperty(UserID.dbField, loginAppManager.connectedUser!!.userId)
            jsonObject.addProperty(DeckNumber.dbField, deckToOpen)
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
                jsonObject.addProperty(NewWallet.dbField, actualValue - cost)
                val userId = loginAppManager.connectedUser!!.userId!!
                responseFromApiLiveData = rickAndMortyAPI.updateWalletValue(jsonObject, userId)
                responseFromApiLiveData.observeOnce(Observer {
                    updateWalletTreatment(it)
                })
            } else {
                Toast.makeText(context, context.getString(R.string.not_enought_money), Toast.LENGTH_SHORT).show()
                transactionInProgress = false
            }

        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
            transactionInProgress = false
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
            CardBooster.LITTLE.cost ->  CardBooster.LITTLE.amount
            CardBooster.MEDIUM.cost ->  CardBooster.MEDIUM.amount
            CardBooster.LARGE.cost ->  CardBooster.LARGE.amount
            else -> 0
        }
    }

}