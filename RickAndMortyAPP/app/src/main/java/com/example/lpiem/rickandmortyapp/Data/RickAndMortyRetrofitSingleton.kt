package com.example.lpiem.rickandmortyapp.Data


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
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

class RickAndMortyRetrofitSingleton private constructor(private val context: Context) {

    companion object : SingletonHolder<RickAndMortyRetrofitSingleton, Context>(::RickAndMortyRetrofitSingleton)

    private var rickAndMortyAPIInstance: RickAndMortyAPI? = null
    private var currentCall: Call<*>? = null

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

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes): MutableLiveData<Any> {

        val liveData =  MutableLiveData<Any>()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    when (type) {
                        LIST_OF_CARDS,
                        ADD_CARD_TO_MARKET,
                        OPEN_RANDOM_DECK -> {
                            liveData.postValue(result as ListOfCards)
                        }
                        GET_CARD_DETAILS -> {
                            liveData.postValue(result as DetailledCard)
                        }
                        KAAMELOTT_QUOTE -> {
                            liveData.postValue(result as KaamlottQuote)
                        }
                        LOGIN,
                        GET_USER_BY_ID,
                        PUT_DATE,
                        UPDATE_WALLET,
                        DECKS_INCREASED,
                        SIGN_IN,
                        CONNECTION,
                        UPDATE_USER_INFO -> {
                            liveData.postValue(result as ResponseFromApi)
                        }
                        GET_WALLET,
                        BUY_BOOSTER -> {
                            liveData.postValue(result as Wallet)
                        }
                        GET_FAQ -> {
                            liveData.postValue(result as ListOfFAQ)
                        }
                        LIST_OF_FRIENDS -> TODO()
                        RESULT_FRIENDS_SEARCHING -> TODO()
                        ADD_A_FRIENDS -> TODO()
                        DEL_A_FRIEND -> TODO()
                        ACCEPT_FRIENDSHIP -> TODO()
                        RESPONSE_FROM_API -> TODO()
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

    fun addCardToMarket(userId: Int, card: Card, price: Int): MutableLiveData<ListOfCards> {
        val jsonBody = JsonObject()
        jsonBody.addProperty("card_name", card.cardName)
        jsonBody.addProperty("price", price)
        currentCall = instance!!.addCardToMarket(userId, card.cardId!!, jsonBody)
        return callRetrofit(currentCall!!, ADD_CARD_TO_MARKET) as MutableLiveData<ListOfCards>
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

    fun login(jsonBody: JsonObject): MutableLiveData<ResponseFromApi> {
        currentCall = instance!!.connectUser(jsonBody)
        return callRetrofit(currentCall!!, LOGIN) as MutableLiveData<ResponseFromApi>
    }

    fun buyBooster(userId: Int): MutableLiveData<Wallet> {
        currentCall = instance!!.getWallet(userId)
        return callRetrofit(currentCall!!, BUY_BOOSTER) as MutableLiveData<Wallet>
    }

    fun updateWalletValue(jsonObject: JsonObject, userId: Int): MutableLiveData<ResponseFromApi> {
        currentCall = instance!!.updateWallet(userId, jsonObject)
        return callRetrofit(currentCall!!, UPDATE_WALLET) as MutableLiveData<ResponseFromApi>
    }

    fun increaseDeckNumber(jsonObject: JsonObject): MutableLiveData<ResponseFromApi> {
        currentCall = instance!!.increaseNumberOfDecks(jsonObject)
        return callRetrofit(currentCall!!, DECKS_INCREASED) as MutableLiveData<ResponseFromApi>
    }

    fun signIn(userName: String, email: String, password: String): MutableLiveData<ResponseFromApi> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.UserName.string, userName)
        jsonBody.addProperty(JsonProperty.UserEmail.string, email)
        jsonBody.addProperty(JsonProperty.UserPassword.string, password)
        currentCall = instance!!.signInUser(jsonBody)
        return callRetrofit(currentCall!!, SIGN_IN) as MutableLiveData<ResponseFromApi>
    }

    fun regularConnection(email: String, password: String): MutableLiveData<ResponseFromApi> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.UserEmail.string, email)
        jsonBody.addProperty(JsonProperty.UserPassword.string, password)
        currentCall = instance!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$currentCall")
        return callRetrofit(currentCall!!, RetrofitCallTypes.CONNECTION) as MutableLiveData<ResponseFromApi>
    }

    fun openRandomDeck(userId: Int?): MutableLiveData<ListOfCards> {
        currentCall = instance!!.getRandomDeck(userId!!)
        return callRetrofit(currentCall!!, RetrofitCallTypes.OPEN_RANDOM_DECK) as MutableLiveData<ListOfCards>
    }

    fun updateUserInfo(userId: Int?): MutableLiveData<ResponseFromApi> {
        val updateUser = instance!!.getUserById(userId!!)
        return callRetrofit(updateUser, RetrofitCallTypes.UPDATE_USER_INFO) as MutableLiveData<ResponseFromApi>
    }

}

