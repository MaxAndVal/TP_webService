package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.view.View
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Presenter.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.OpenDeck.OpenDeckActivity
import com.example.lpiem.rickandmortyapp.View.OpenDeck.OpenDecksInterface
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_open_deck.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenDeckManager  private constructor(private val context: Context) {

    internal val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private lateinit var link: OpenDecksInterface
    var listOfnewCards : List<Card>? = null

    companion object : SingletonHolder<OpenDeckManager, Context>(::OpenDeckManager)

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.OPEN_RANDOM_DECK -> {
                            openRandomDeckTreatment(result as ListOfCards)
                        }
                        RetrofitCallTypes.UPDATE_USER_INFO ->{
                            var homeManager = HomeManager.getInstance(context)
                            homeManager.updateUserInfo(result as ResponseFromApi)
                            link.showAnimation(false)
                            val user = loginAppManager.connectedUser
                            link.updateDecksCount(user!!.deckToOpen!!)
                        }
                    }

                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: " + responseError.string() )
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    fun openRandomDeckTreatment(listOfCards: ListOfCards){

        val user = loginAppManager.connectedUser
        context as OpenDeckActivity
        DetailCollectionManager.getInstance(context).listOfNewCards = listOfCards.cards as MutableList<Card>
        val animationLoop = context.av_from_code
        animationLoop.setAnimation("portal_loop.json")
        animationLoop.pauseAnimation()
        context.fl_animation.visibility = View.GONE
        context.fl_DeckToOpen.visibility = View.VISIBLE
        context.tv_openYourDeck.text = "You have ${user!!.deckToOpen} deck to open"

        context.getInfoNewCards()

        val updateUser = rickAndMortyAPI!!.getUserById(user!!.userId!!)
        callRetrofit(updateUser, RetrofitCallTypes.UPDATE_USER_INFO)
    }

    fun openRandomDeck(deckToOpen: Int?, link: OpenDecksInterface) {
        this.link = link
        link.showAnimation(true)

        val user = loginAppManager.connectedUser
        if(deckToOpen!! >0){
            val openADeck = rickAndMortyAPI!!.getRandomDeck(user!!.userId!!)
            callRetrofit(openADeck, RetrofitCallTypes.OPEN_RANDOM_DECK)
        }
    }

}