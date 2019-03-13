package com.example.lpiem.rickandmortyapp.Manager.Market

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import retrofit2.Call

class MarketManager private constructor(private val context: Context) {

    internal var rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var currentCall: Call<*>? = null
    var cardListDisplay = MutableLiveData<ListOfCards>()
    private var marketLiveData = MutableLiveData<ListOfCards>()
    private var marketResponseLiveData = MutableLiveData<ResponseFromApi>()
    private var friendId = -1

    companion object : SingletonHolder<MarketManager, Context>(::MarketManager)


    fun cancelCall() {
        currentCall?.cancel()
        Log.d(TAG, "call canceled")
    }

    private fun buyCardTreatment(responseFromApi: ResponseFromApi) {
        if(responseFromApi.code==200){
            Toast.makeText(context, "Congrats, you got a new card", Toast.LENGTH_SHORT).show()
            getMarket(responseFromApi.results, friendId)
        }else{
            Toast.makeText(context, "Error :"+responseFromApi.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        (context as MarketActivity).listOfCards = response
        val list = context.listOfCards
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

    fun buyCard(card: Card?, userId: Int?, friendId: Int?) {
        marketResponseLiveData = rickAndMortyAPI.buyCard(card, userId, friendId)
        marketResponseLiveData.observeOnce(Observer {
            buyCardTreatment(it!!)
        })
    }
}