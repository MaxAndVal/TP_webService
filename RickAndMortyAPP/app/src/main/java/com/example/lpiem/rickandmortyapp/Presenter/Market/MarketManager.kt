package com.example.lpiem.rickandmortyapp.Presenter.Market

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketManager private constructor(private val context: Context) {

    private var rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var currentCall: Call<*>? = null
    lateinit var cardListDisplay: CardListDisplay

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

    private fun listOfCardTreatment(response: ListOfCards) {
        (context as MarketActivity).listOfCards = response
        val list = context.listOfCards
        if (list?.code == SUCCESS) {
            cardListDisplay.displayResult(list)
        } else {
            Toast.makeText(context, "erreur code ${list?.code} message : ${list?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMarket(user: User?, link: CardListDisplay, friend_id: Int?) {
        cardListDisplay = link
        val userId = user?.userId ?: -1
        if (friend_id != null) {
            currentCall = rickAndMortyAPI!!.getFriendMarket(userId, friend_id)
        } else {
            currentCall = rickAndMortyAPI!!.getUserMarket(userId)
        }
        callRetrofit(currentCall!!, RetrofitCallTypes.LIST_OF_CARDS)
    }
}