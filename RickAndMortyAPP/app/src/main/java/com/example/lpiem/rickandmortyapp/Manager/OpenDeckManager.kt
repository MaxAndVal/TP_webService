package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Manager.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce

class OpenDeckManager  private constructor(private val context: Context) {

    internal val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    var showDetails = true
    private var listOfCardsLiveData = MutableLiveData<ListOfCards>()
    private var responseFromApiLiveData = MutableLiveData<ResponseFromApi>()
    var updateDeckCountLiveData = MutableLiveData<Int>()
    var infoNewCardLiveData = MutableLiveData<Int>()

    companion object : SingletonHolder<OpenDeckManager, Context>(::OpenDeckManager)


    private fun openRandomDeckTreatment(listOfCards: ListOfCards) {
        val user = loginAppManager.connectedUser
        val userId = user!!.userId
        DetailCollectionManager.getInstance(context).listOfNewCards = listOfCards.cards as MutableList<Card>

        updateDeckCountLiveData.postValue(user.deckToOpen!!)

        if (showDetails) infoNewCardLiveData.postValue(user.deckToOpen!!)

        responseFromApiLiveData = rickAndMortyAPI.updateUserInfo(userId)
        responseFromApiLiveData.observeOnce(Observer {
            updateUserInfoTreatment(it)
        })
    }

    private fun updateUserInfoTreatment(result: ResponseFromApi) {
        val homeManager = HomeManager.getInstance(context)
        homeManager.updateUserInfo(result)
        val user = loginAppManager.connectedUser
        val decks = user!!.deckToOpen!!
        updateDeckCountLiveData.postValue(decks)
    }


    fun openRandomDeck(deckToOpen: Int?) {
        val userId = loginAppManager.connectedUser!!.userId
        if(deckToOpen!! > 0) {
            listOfCardsLiveData = rickAndMortyAPI.openRandomDeck(userId)
            listOfCardsLiveData.observeOnce(Observer {
                openRandomDeckTreatment(it)
            })
        }
    }

}