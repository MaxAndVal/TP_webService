package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.view.View
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.OpenDeckActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_open_deck.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenDeckManager  private constructor(private val context: Context) {

    internal val loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance

    fun openRandomDeck(deckToOpen: Int?) {
        (context as OpenDeckActivity).fl_DeckToOpen.visibility = View.GONE
        context.fl_animation.visibility = View.VISIBLE
        var animationLoop = (context as OpenDeckActivity).av_from_code
        animationLoop.setAnimation("portal_loop.json")
        animationLoop.playAnimation()
        animationLoop.loop(true)

        var user = loginAppManager.connectedUser
        if(deckToOpen!! >0){
            var openADeck = rickAndMortyAPI!!.getRandomDeck(user!!.userId!!)
            callRetrofit(openADeck, RetrofitCallTypes.OPEN_RANDOM_DECK)
        }
    }

    init {

    }



    companion object : SingletonHolder<OpenDeckManager, Context>(::OpenDeckManager)

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.OPEN_RANDOM_DECK -> {
                            var user = loginAppManager.connectedUser
                            var updateUser = rickAndMortyAPI!!.getUserById(user!!.userId!!)
                            callRetrofit(updateUser, RetrofitCallTypes.UPDATE_USER_INFO)
                        }
                        RetrofitCallTypes.UPDATE_USER_INFO ->{
                            var homeManager = HomeManager.getInstance(context)
                            homeManager.updateUserInfo(result as ResponseFromApi)
                            var animationLoop = (context as OpenDeckActivity).av_from_code
                            animationLoop.setAnimation("portal_loop.json")
                            animationLoop.pauseAnimation()
                            context.fl_animation.visibility = View.GONE
                            (context as OpenDeckActivity).fl_DeckToOpen.visibility = View.VISIBLE
                            var user = loginAppManager.connectedUser
                            context.tv_openYourDeck.text = "You have ${user!!.deckToOpen} deck to open"

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


}