package com.example.lpiem.rickandmortyapp.ViewModel.BackActivity

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Card
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.Home.HomeManager
import com.example.lpiem.rickandmortyapp.ViewModel.collection.DetailCollectionManager

class OpenDeckManager  private constructor(private val context: Context) {

    internal val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    var showDetails = true
    private var listOfCardsLiveData = MutableLiveData<ListOfCards>()
    private var responseFromApiLiveData = MutableLiveData<UserResponse>()
    var updateDeckCountLiveData = MutableLiveData<Int>()
    var infoNewCardLiveData = MutableLiveData<Int>()

    companion object : SingletonHolder<OpenDeckManager, Context>(::OpenDeckManager)

    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

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

    private fun updateUserInfoTreatment(result: UserResponse) {
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