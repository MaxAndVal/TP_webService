package com.example.lpiem.rickandmortyapp.Manager.collection

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment

class CollectionManager private constructor(private val context: Context) {

    private var rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var collectionLiveData = MutableLiveData<ListOfCards>()


    var collectionFragment: CollectionFragment? = null
    lateinit var cardListDisplay: CardListDisplay

    companion object : SingletonHolder<CollectionManager, Context>(::CollectionManager)


    fun captureFragmentInstance(fragment: CollectionFragment) {
        collectionFragment = fragment
    }

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }


    private fun addCardToMarket() {
        Toast.makeText(context, "Card is now on the market !", Toast.LENGTH_LONG).show()
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        collectionFragment!!.listOfCards = response
        val list = collectionFragment!!.listOfCards
        if (list?.code == SUCCESS) {
            cardListDisplay.displayResult(list)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), list?.code, list?.message), Toast.LENGTH_SHORT).show()
        }
    }

    fun getListOfDecks(user: User?, link: CardListDisplay) {
        cardListDisplay = link
        val userId = user?.userId ?: -1
        collectionLiveData = rickAndMortyAPI.listOfCardsForCollectionList(userId)
        collectionLiveData.observeOnce(Observer {
            listOfCardTreatment(it!!)
        })
    }

    fun sellACard(userId: Int, card: Card, price: Int) {
        collectionLiveData = rickAndMortyAPI.addCardToMarket(userId, card, price)
        collectionLiveData.observeOnce(Observer {
            addCardToMarket()
        })
    }
}