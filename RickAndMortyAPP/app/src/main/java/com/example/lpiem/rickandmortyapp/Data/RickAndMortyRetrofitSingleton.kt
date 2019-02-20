package com.example.lpiem.rickandmortyapp.Data


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Manager.collection.CollectionManager
import com.example.lpiem.rickandmortyapp.Manager.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.Model.*
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
    private lateinit var detailCollectionManager: DetailCollectionManager

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

    fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes): MutableLiveData<Any> {

        val liveData =  MutableLiveData<Any>()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        LIST_OF_CARDS -> {
                            liveData.postValue(result as ListOfCards)
                        }
                        ADD_CARD_TO_MARKET -> {
                            //TODO : reste à faire décrémenter plus vérif code 200
                            collectionManager = CollectionManager.getInstance(context)
                            collectionManager.addCardToMarket()
                            //liveData.postValue(result as ListOfCards)
                        }
                        GET_CARD_DETAILS -> {
                            //detailCollectionManager.getCardDetailTreatment(result as DetailledCard)
                            liveData.postValue(result as DetailledCard)
                        }
                        RESPONSE_FROM_API -> {

                        }
                        KAAMELOTT_QUOTE -> {
                            liveData.postValue(result as KaamlottQuote)
                        }
                        SIGN_IN -> TODO()
                        CONNECTION -> TODO()
                        LIST_OF_FRIENDS -> TODO()
                        LOGIN -> TODO()
                        RESULT_FRIENDS_SEARCHING -> TODO()
                        ADD_A_FRIENDS -> TODO()
                        DEL_A_FRIEND -> TODO()
                        GET_USER_BY_ID -> {
                            liveData.postValue(result as ResponseFromApi)
                        }
                        PUT_DATE -> {
                            liveData.postValue(result as ResponseFromApi)
                        }
                        UPDATE_WALLET -> {
                            liveData.postValue(result as ResponseFromApi)
                        }
                        GET_WALLET -> {
                            liveData.postValue(result as Wallet)
                        }
                        ACCEPT_FRIENDSHIP -> TODO()
                        BUY_BOOSTER -> TODO()
                        DECKS_INCREASED -> TODO()
                        GET_FAQ -> {
                            liveData.postValue(result as ListOfFAQ)
                        }
                        OPEN_RANDOM_DECK -> TODO()
                        UPDATE_USER_INFO -> TODO()
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
        return liveData

    }

    fun listOfCardsForCollectionList(userId: Int): MutableLiveData<ListOfCards> {
        currentCall = instance!!.getListOfCardsById(userId)
        return callRetrofit(currentCall!!, LIST_OF_CARDS) as MutableLiveData<ListOfCards>
    }


    fun addCardToMarket(userId: Int, card: Card, price: Int) {
        val jsonBody = JsonObject()
        jsonBody.addProperty("card_name", card.cardName)
        jsonBody.addProperty("price", price)
        currentCall = instance!!.addCardToMarket(userId, card.cardId!!, jsonBody)
        callRetrofit(currentCall!!, ADD_CARD_TO_MARKET)
    }

    fun getDetail(id: Int): MutableLiveData<DetailledCard> {
        currentCall = instance!!.getCardDetails(id)
        return callRetrofit(currentCall!!, GET_CARD_DETAILS) as MutableLiveData<DetailledCard>
    }

    fun getRandomQuote(): MutableLiveData<KaamlottQuote> {
        currentCall = instance!!.getRandomQuote()
        return callRetrofit(currentCall!!, KAAMELOTT_QUOTE) as MutableLiveData<KaamlottQuote>
    }

    fun getUserById(userId: Int?): MutableLiveData<ResponseFromApi> {
        currentCall = instance!!.getUserById(userId!!)
       return callRetrofit(currentCall!!, GET_USER_BY_ID) as MutableLiveData<ResponseFromApi>
    }

    fun putDateToken(date: String, id: Int?): MutableLiveData<ResponseFromApi> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.NewDate.string, date)
        currentCall = instance!!.putNewDate(id!!, jsonBody)
        return callRetrofit(currentCall!!, PUT_DATE) as MutableLiveData<ResponseFromApi>
    }

    fun updateWallet(score: Int, user: User?): MutableLiveData<ResponseFromApi> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.NewWallet.string, (user!!.userWallet!! + (score * 10)))
        currentCall = instance!!.updateWallet(user.userId!!, jsonBody)
        return callRetrofit(currentCall!!, UPDATE_WALLET) as MutableLiveData<ResponseFromApi>
    }

    fun getWallet(id: Int?): MutableLiveData<Wallet> {
        currentCall = instance!!.getWallet(id!!)
        return callRetrofit(currentCall!!, GET_WALLET) as MutableLiveData<Wallet>
    }

    fun getFAQ(): MutableLiveData<ListOfFAQ> {
        currentCall = instance!!.getFAQ()
        return callRetrofit(currentCall!!, RetrofitCallTypes.GET_FAQ) as MutableLiveData<ListOfFAQ>
    }

}

