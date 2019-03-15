package com.example.lpiem.rickandmortyapp.Data


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.*
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.SignInManager
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
                    Log.d(TAG, result.toString())
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
                        RESPONSE_FROM_API,
                        LOGIN,
                        GET_USER_BY_ID,
                        PUT_DATE,
                        UPDATE_WALLET,
                        DECKS_INCREASED,
                        SIGN_IN,
                        CONNECTION,
                        UPDATE_USER_INFO,
                        ADD_A_FRIENDS,
                        ACCEPT_FRIENDSHIP,
                        DEL_A_FRIEND -> {
                            liveData.postValue(result as UserResponse)
                        }
                        GET_WALLET,
                        BUY_BOOSTER -> {
                            liveData.postValue(result as Wallet)
                        }
                        GET_FAQ -> {
                            liveData.postValue(result as ListOfFAQ)
                        }
                        BUY_CAR_FROM_FRIEND -> {
                            liveData.postValue(result as UserResponse)
                        }
                        LIST_OF_FRIENDS,
                        RESULT_FRIENDS_SEARCHING -> {
                            liveData.postValue(result as ListOfFriends)
                        }
                        ADD_A_FRIENDS -> TODO()
                        DEL_A_FRIEND -> TODO()
                        ACCEPT_FRIENDSHIP -> TODO()
                        LOST_CODE,
                        CHANGE_PASSWORD->{
                        liveData.postValue(result as UserResponse)
                    }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    if (type == LOGIN) {
                        val loginAppManager = LoginAppManager.getInstance(context)
                        loginAppManager.loaderDisplay.postValue(View.GONE)
                    }
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
                if (type == LOGIN) {
                    Toast.makeText(context, "Une erreur a eu lieu. Merci de tenter de vous reconnecter à nouveau", Toast.LENGTH_SHORT).show()
                    val loginAppManager = LoginAppManager.getInstance(context)
                    loginAppManager.loaderDisplay.postValue(View.GONE)
                } else if (type == SIGN_IN) {
                    Toast.makeText(context, "Une erreur a eu lieu. Merci de tenter de vous reconnecter à nouveau", Toast.LENGTH_SHORT).show()
                    val signInManager = SignInManager.getInstance(context)
                    signInManager.loaderLiveData.postValue(View.GONE)
                }
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

    fun getUserById(userId: Int?): MutableLiveData<UserResponse> {
        currentCall = instance!!.getUserById(userId!!)
       return callRetrofit(currentCall!!, GET_USER_BY_ID) as MutableLiveData<UserResponse>
    }

    fun putDateToken(date: String, id: Int?): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.NewDate.string, date)
        currentCall = instance!!.putNewDate(id!!, jsonBody)
        return callRetrofit(currentCall!!, PUT_DATE) as MutableLiveData<UserResponse>
    }
    fun putMemoryDateToken(date: String, id: Int?): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.NewDate.string, date)
        currentCall = instance!!.putNewMemoryDate(id!!, jsonBody)
        return callRetrofit(currentCall!!, PUT_DATE) as MutableLiveData<UserResponse>
    }

    fun updateWallet(score: Int, user: User?): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.NewWallet.string, (user!!.userWallet!! + (score * 10)))
        currentCall = instance!!.updateWallet(user.userId!!, jsonBody)
        return callRetrofit(currentCall!!, UPDATE_WALLET) as MutableLiveData<UserResponse>
    }

    fun getWallet(id: Int?): MutableLiveData<Wallet> {
        currentCall = instance!!.getWallet(id!!)
        return callRetrofit(currentCall!!, GET_WALLET) as MutableLiveData<Wallet>
    }

    fun getFAQ(): MutableLiveData<ListOfFAQ> {
        currentCall = instance!!.getFAQ()
        return callRetrofit(currentCall!!, RetrofitCallTypes.GET_FAQ) as MutableLiveData<ListOfFAQ>
    }

    fun login(jsonBody: JsonObject): MutableLiveData<UserResponse> {
        currentCall = instance!!.connectUser(jsonBody)
        return callRetrofit(currentCall!!, LOGIN) as MutableLiveData<UserResponse>
    }

    fun buyBooster(userId: Int): MutableLiveData<Wallet> {
        currentCall = instance!!.getWallet(userId)
        return callRetrofit(currentCall!!, BUY_BOOSTER) as MutableLiveData<Wallet>
    }

    fun updateWalletValue(jsonObject: JsonObject, userId: Int): MutableLiveData<UserResponse> {
        currentCall = instance!!.updateWallet(userId, jsonObject)
        return callRetrofit(currentCall!!, UPDATE_WALLET) as MutableLiveData<UserResponse>
    }

    fun increaseDeckNumber(jsonObject: JsonObject): MutableLiveData<UserResponse> {
        currentCall = instance!!.increaseNumberOfDecks(jsonObject)
        return callRetrofit(currentCall!!, DECKS_INCREASED) as MutableLiveData<UserResponse>
    }

    fun signIn(userName: String, email: String, password: String): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.UserName.string, userName)
        jsonBody.addProperty(JsonProperty.UserEmail.string, email)
        jsonBody.addProperty(JsonProperty.UserPassword.string, password)
        currentCall = instance!!.signInUser(jsonBody)
        return callRetrofit(currentCall!!, SIGN_IN) as MutableLiveData<UserResponse>
    }

    fun regularConnection(email: String, password: String): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        jsonBody.addProperty(JsonProperty.UserEmail.string, email)
        jsonBody.addProperty(JsonProperty.UserPassword.string, password)
        currentCall = instance!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$currentCall")
        return callRetrofit(currentCall!!, RetrofitCallTypes.CONNECTION) as MutableLiveData<UserResponse>
    }

    fun openRandomDeck(userId: Int?): MutableLiveData<ListOfCards> {
        currentCall = instance!!.getRandomDeck(userId!!)
        return callRetrofit(currentCall!!, RetrofitCallTypes.OPEN_RANDOM_DECK) as MutableLiveData<ListOfCards>
    }

    fun updateUserInfo(userId: Int?): MutableLiveData<UserResponse> {
        currentCall = instance!!.getUserById(userId!!)
        return callRetrofit(currentCall!!, RetrofitCallTypes.UPDATE_USER_INFO) as MutableLiveData<UserResponse>
    }

    fun getCardList(amount: Int): MutableLiveData<ListOfCards> {
        currentCall = instance!!.getCardSelection(amount)
        return callRetrofit(currentCall!!, LIST_OF_CARDS) as MutableLiveData<ListOfCards>
    }

    fun getMarket(user: User?, friendId: Int?): MutableLiveData<ListOfCards> {

        val userId = user?.userId ?: -1
        currentCall = if (friendId != null) {
           instance!!.getFriendMarket(userId, friendId)
        } else {
            instance!!.getUserMarket(userId)
        }
        return  callRetrofit(currentCall!!, LIST_OF_CARDS) as MutableLiveData<ListOfCards>
    }

    fun buyCard(card: Card?, userId: Int?, friendId: Int?): MutableLiveData<UserResponse> {
        val jsonObject = JsonObject()
        if (card != null) {
            jsonObject.addProperty("price", card.price)
        }
        currentCall = instance!!.buyCardFromFriend(userId!!, friendId!!, card!!.cardId!!, jsonObject)
        return callRetrofit(currentCall!!, BUY_CAR_FROM_FRIEND) as MutableLiveData<UserResponse>
    }

    fun addRewardsToUser(rewards: MutableList<MemoryReward>, userId: Int): MutableLiveData<UserResponse> {
        val jsonBody = JsonObject()
        val gson = GsonBuilder().create()
        jsonBody.add("listOfCards", gson.toJsonTree(rewards))
        jsonBody.addProperty("user_id", userId)
        currentCall = instance!!.addRewards(jsonBody)
        return callRetrofit(currentCall!!, RESPONSE_FROM_API) as MutableLiveData<UserResponse>
    }

    fun changePassword(userId: Int?, userEmail: String?, oldPass: String, newPass: String): MutableLiveData<UserResponse> {
    val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", userEmail)
        jsonBody.addProperty("user_old_password", oldPass)
        jsonBody.addProperty("user_new_password", newPass)
        currentCall = instance!!.changePassword(userId!!,jsonBody)
        return callRetrofit(currentCall!!, CHANGE_PASSWORD) as MutableLiveData<UserResponse>
    }

    fun sendCode(jsonBody: JsonObject): MutableLiveData<UserResponse> {
        currentCall = instance!!.sendCodeForPassword(jsonBody)
        return callRetrofit(currentCall!!, LOST_CODE) as MutableLiveData<UserResponse>
    }

    fun getFriendsList(userId: Int): MutableLiveData<ListOfFriends> {
        currentCall = instance!!.getListOfFriends(userId)
        return callRetrofit(currentCall!!, RetrofitCallTypes.LIST_OF_FRIENDS) as MutableLiveData<ListOfFriends>
    }

    fun getFriendSearchResult(userId: Int, friends: String?): MutableLiveData<ListOfFriends> {
        currentCall = instance!!.searchForFriends(userId, friends)
        return callRetrofit(currentCall!!, RetrofitCallTypes.RESULT_FRIENDS_SEARCHING) as MutableLiveData<ListOfFriends>
    }

    fun addThisFriend(currentUserId: Int, friendId: Int): MutableLiveData<UserResponse> {
        currentCall = instance!!.addAFriend(currentUserId, friendId)
        return callRetrofit(currentCall!!, RetrofitCallTypes.ADD_A_FRIENDS) as MutableLiveData<UserResponse>
    }

    fun validateFriendship(currentUserId: Int, friendId: Int): MutableLiveData<UserResponse> {
        currentCall = instance!!.validateAFriend(currentUserId, friendId)
        return callRetrofit(currentCall!!, RetrofitCallTypes.ACCEPT_FRIENDSHIP) as MutableLiveData<UserResponse>
    }

    fun deleteThisFriend(currentUserId: Int, friendId: Int): MutableLiveData<UserResponse> {
        currentCall = instance!!.deleteAFriend( currentUserId,friendId)
        return callRetrofit(currentCall!!, RetrofitCallTypes.DEL_A_FRIEND) as MutableLiveData<UserResponse>
    }

    fun loginWithToken(token: JsonObject): MutableLiveData<UserResponse> {
        currentCall = instance!!.connectUser(token)
        return callRetrofit(currentCall!!, CONNECTION) as MutableLiveData<UserResponse>
    }

}

