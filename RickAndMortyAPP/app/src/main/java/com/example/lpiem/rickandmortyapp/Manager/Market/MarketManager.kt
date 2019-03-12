package com.example.lpiem.rickandmortyapp.Manager.Market

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketManager private constructor(private val context: Context) {

    internal var rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var currentCall: Call<*>? = null
    lateinit var cardListDisplay: CardListDisplay
    private var marketLiveData = MutableLiveData<ListOfCards>()
    private var marketResponseLiveData = MutableLiveData<ResponseFromApi>()


    var friendId = -1

    companion object : SingletonHolder<MarketManager, Context>(::MarketManager)


    fun cancelCall() {
        currentCall?.cancel()
        Log.d(TAG, "call canceled")
    }
    //listOfCardTreatment
    //buyCardTreatment

    private fun buyCardTreatment(responseFromApi: ResponseFromApi) {
        if(responseFromApi.code==200){
            Toast.makeText(context, "Congrats, you got a new card", Toast.LENGTH_SHORT).show()
            getMarket(responseFromApi.results, cardListDisplay, friendId)
        }else{
            Toast.makeText(context, "Error :"+responseFromApi.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        (context as MarketActivity).listOfCards = response
        val list = context.listOfCards
        if (list?.code == SUCCESS) {
            cardListDisplay.displayResult(list)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), list?.code, list?.message), Toast.LENGTH_SHORT).show()
        }
    }

    fun getMarket(user: User?, link: CardListDisplay, friendId: Int?) {
        cardListDisplay = link
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