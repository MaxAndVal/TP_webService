package com.example.lpiem.rickandmortyapp.Presenter.collection

import android.content.Context
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import retrofit2.Call

class CollectionManager  {

    private var rickAndMortyAPI: RickAndMortyRetrofitSingleton
    private val context: Context

    private constructor(context: Context) {
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
        this.context = context
    }

    var collectionFragment: CollectionFragment? = null
    private var currentCall : Call<*>? = null
    lateinit var cardListDisplay : CardListDisplay

    companion object : SingletonHolder<CollectionManager, Context>(::CollectionManager)

    fun captureFragmentInstance(fragment: CollectionFragment) {
        collectionFragment = fragment
    }

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
        //currentCall?.cancel()
    }


     fun addCardtoMarket() {
        Toast.makeText(context, "Card is now on the market !", Toast.LENGTH_LONG).show()
    }

     fun listOfCardTreatment(response: ListOfCards) {
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
        rickAndMortyAPI.listOfCardsForCollectionList(userId)
        //currentCall = rickAndMortyAPI.instance!!.getListOfCardsById(userId)
        //rickAndMortyAPI.callRetrofit(currentCall!!, LIST_OF_CARDS)
    }

    fun sellACard(userId: Int, card:Card, price: Int) {
        rickAndMortyAPI.addCardToMarket(userId, card, price)
//        val jsonBody = JsonObject()
//        jsonBody.addProperty("card_name", card.cardName)
//        jsonBody.addProperty("price", price)
//        val sellCall = rickAndMortyAPI.instance!!.addCardToMarket(userId, card.cardId!!, jsonBody)
//        rickAndMortyAPI.callRetrofit(sellCall, ADD_CARD_TO_MARKET)
    }
}