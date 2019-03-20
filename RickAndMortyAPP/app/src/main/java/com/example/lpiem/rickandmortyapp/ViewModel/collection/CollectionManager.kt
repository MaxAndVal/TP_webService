package com.example.lpiem.rickandmortyapp.ViewModel.collection

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Card
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce

class CollectionManager private constructor(private val context: Context) {

    private var rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var collectionLiveData = MutableLiveData<ListOfCards>()
    var listOfCards: ListOfCards? = null
    var isAddCardSuccededLiveData = MutableLiveData<Boolean>()
    lateinit var cardListDisplay: MutableLiveData<ListOfCards>

    companion object : SingletonHolder<CollectionManager, Context>(::CollectionManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }


    private fun addCardToMarket(it: ListOfCards) {
        if(it.code == SUCCESS){
            Toast.makeText(context, "Card is now on the market !", Toast.LENGTH_LONG).show()
            isAddCardSuccededLiveData.postValue(true)
        }else{
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            isAddCardSuccededLiveData.postValue(false)
        }
    }

    private fun listOfCardTreatment(response: ListOfCards) {
        listOfCards = response
        if (listOfCards?.code == SUCCESS) {
            cardListDisplay.postValue(listOfCards)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), listOfCards?.code, listOfCards?.message), Toast.LENGTH_SHORT).show()
        }
    }

    fun getListOfDecks(user: User?, UILink: MutableLiveData<ListOfCards>) {
        cardListDisplay = UILink
        val userId = user?.userId ?: -1
        collectionLiveData = rickAndMortyAPI.listOfCardsForCollectionList(userId)
        collectionLiveData.observeOnce(Observer {
            listOfCardTreatment(it!!)
        })
    }

    fun sellACard(user: User, card: Card, price: Int) {
        collectionLiveData = rickAndMortyAPI.addCardToMarket(user.userId!!, card, price)
        collectionLiveData.observeOnce(Observer {
            addCardToMarket(it)
        })
    }
}