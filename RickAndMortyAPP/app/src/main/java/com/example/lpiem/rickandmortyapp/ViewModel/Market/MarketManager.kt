package com.example.lpiem.rickandmortyapp.ViewModel.Market

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Card
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Collection.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import retrofit2.Call

class MarketManager private constructor(private val context: Context) {

    internal var rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var currentCall: Call<*>? = null
    var cardListDisplay = MutableLiveData<ListOfCards>()
    private var marketLiveData = MutableLiveData<ListOfCards>()
    private var marketResponseLiveData = MutableLiveData<UserResponse>()
    var updateUserInfoLiveData = MutableLiveData<Int>()
    private var friendId = -1
    var listOfCards: ListOfCards? = null
    private var isLoading = false

    companion object : SingletonHolder<MarketManager, Context>(::MarketManager)


    fun cancelCall() {
        currentCall?.cancel()
        Log.d(TAG, "call canceled")
    }

    private fun buyCardTreatment(userResponse: UserResponse, cardPrice : Int) {
        if (userResponse.code == 200) {
            Toast.makeText(context, "Congrats, you got a new card", Toast.LENGTH_SHORT).show()
            getMarket(userResponse.user, friendId)
            updateUserInfoLiveData.postValue(cardPrice)
        } else {
            Toast.makeText(context, "Error :" + userResponse.message, Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        listOfCards = response
        val list = listOfCards
        if (list?.code == SUCCESS) {
            cardListDisplay.postValue(list)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), list?.code, list?.message), Toast.LENGTH_SHORT).show()
        }
    }

    fun getMarket(user: User?, friendId: Int?) {
        if (friendId != null) {
            this.friendId = friendId
        }
        marketLiveData = rickAndMortyAPI.getMarket(user, friendId)
        marketLiveData.observeOnce(Observer {
            listOfCardTreatment(it!!)
        })
    }

    fun buyCard(card: Card?, user: User?, friendId: Int?) {
        var newWallet = user!!.userWallet!! - card!!.price!!
        if (!isLoading) {
            marketResponseLiveData = rickAndMortyAPI.buyCard(card, user.userId, friendId)
            marketResponseLiveData.observeOnce(Observer {
                buyCardTreatment(it!!, newWallet)
            })
        }
    }
}