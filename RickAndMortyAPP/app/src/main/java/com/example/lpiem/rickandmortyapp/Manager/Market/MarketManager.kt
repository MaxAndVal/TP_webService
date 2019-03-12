package com.example.lpiem.rickandmortyapp.Manager.Market

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
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
    var friendId = -1

    companion object : SingletonHolder<MarketManager, Context>(::MarketManager)


    fun cancelCall() {
        currentCall?.cancel()
        Log.d(TAG, "call canceled")
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.LIST_OF_CARDS -> {
                            listOfCardTreatment(result as ListOfCards)
                        }
                        RetrofitCallTypes.BUY_CAR_FROM_FRIEND -> {
                            buyCardTreatment(result as ResponseFromApi)
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
        val userId = user?.userId ?: -1
        currentCall = if (friendId != null) {
            this.friendId = friendId
            rickAndMortyAPI!!.instance!!.getFriendMarket(userId, friendId)
        } else {
            rickAndMortyAPI!!.instance!!.getUserMarket(userId)
        }
        callRetrofit(currentCall!!, RetrofitCallTypes.LIST_OF_CARDS)
    }

    fun buyCard(card: Card?, userId: Int?, friendId: Int?) {

        Log.d(TAG, "cardid:"+ card?.cardId.toString())

        val jsonObject = JsonObject()
        if (card != null) {
            jsonObject.addProperty("price", card.price)
        }
        currentCall = rickAndMortyAPI!!.instance!!.buyCardFromFriend(userId!!, friendId!!, card!!.cardId!!, jsonObject)
        callRetrofit(currentCall!!, RetrofitCallTypes.BUY_CAR_FROM_FRIEND)
    }
}