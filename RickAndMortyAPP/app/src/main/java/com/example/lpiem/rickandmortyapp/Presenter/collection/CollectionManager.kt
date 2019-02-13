package com.example.lpiem.rickandmortyapp.Presenter.collection

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionManager private constructor(private val context: Context) {

    private var rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    var collectionFragment: CollectionFragment? = null
    private var currentCall : Call<*>? = null
    lateinit var cardListDisplay : CardListDisplay

    companion object : SingletonHolder<CollectionManager, Context>(::CollectionManager)

    fun captureFragmentInstance(fragment: CollectionFragment) {
        collectionFragment = fragment
    }

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
                        ADD_CARD_TO_MARKET -> {
                            addCardtoMarket()
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

    private fun addCardtoMarket() {
        Toast.makeText(context, "Card is now on the market !", Toast.LENGTH_LONG).show()
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        collectionFragment!!.listOfCards = response
        val list = collectionFragment!!.listOfCards
        if (list?.code == SUCCESS) {
            cardListDisplay.displayResult(list)
        } else {
            Toast.makeText(context, "erreur code ${list?.code} message : ${list?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getListOfDecks(user: User?, link: CardListDisplay) {
        cardListDisplay = link
        val userId = user?.userId?:-1
        currentCall = rickAndMortyAPI!!.getListOfCardsById(userId)
        callRetrofit(currentCall!!, LIST_OF_CARDS)
    }

    fun sellACard(userId: Int, card:Card, price: Int) {
        val jsonBody = JsonObject()
        jsonBody.addProperty("card_name", card.cardName)
        jsonBody.addProperty("price", price)
        var sellCall = rickAndMortyAPI!!.addCardToMarket(userId, card.cardId!!, jsonBody)
        callRetrofit(sellCall, ADD_CARD_TO_MARKET)
    }
}