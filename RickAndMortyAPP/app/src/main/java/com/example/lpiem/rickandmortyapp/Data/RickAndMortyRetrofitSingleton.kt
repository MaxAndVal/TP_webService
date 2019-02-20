package com.example.lpiem.rickandmortyapp.Data


import android.content.Context
import android.util.Log
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Presenter.collection.CollectionManager
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SUCCESS = 200
private const val BASE_URL = "https://api-rickandmorty-tcg.herokuapp.com"
//private const val BASE_URL = "https://rickandmortyapi.com/"

class RickAndMortyRetrofitSingleton private constructor(private val context: Context) {

    companion object : SingletonHolder<RickAndMortyRetrofitSingleton, Context>(::RickAndMortyRetrofitSingleton)

    private var rickAndMortyAPIInstance: RickAndMortyAPI? = null
    private var currentCall: Call<*>? = null
    private lateinit var collectionManager: CollectionManager

    val instance: RickAndMortyAPI?
        get() {
            if (rickAndMortyAPIInstance == null)
                synchronized(RickAndMortyAPI::class.java) {
                    createApiBuilder()
                }
            return rickAndMortyAPIInstance
        }

    private fun createApiBuilder() {
        val gsonInstance = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonInstance))
                .build()
        rickAndMortyAPIInstance = retrofit.create(RickAndMortyAPI::class.java)
    }

    fun cancelCall() {
        currentCall?.cancel()
        Log.d(TAG, "call canceled")
    }

    fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.LIST_OF_CARDS -> {
                            collectionManager = CollectionManager.getInstance(context)
                            collectionManager.listOfCardTreatment(result as ListOfCards)
                        }
                        RetrofitCallTypes.ADD_CARD_TO_MARKET -> {
                            collectionManager = CollectionManager.getInstance(context)
                            collectionManager.addCardtoMarket()
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

    fun listOfCardsForCollectionList(userId: Int)  {
        currentCall = instance!!.getListOfCardsById(userId)
        callRetrofit(currentCall!!, RetrofitCallTypes.LIST_OF_CARDS)
    }


    fun addCardToMarket(userId: Int, card: Card, price: Int) {
        val jsonBody = JsonObject()
        jsonBody.addProperty("card_name", card.cardName)
        jsonBody.addProperty("price", price)
        currentCall = instance!!.addCardToMarket(userId, card.cardId!!, jsonBody)
        callRetrofit(currentCall!!, RetrofitCallTypes.ADD_CARD_TO_MARKET)
    }

}

