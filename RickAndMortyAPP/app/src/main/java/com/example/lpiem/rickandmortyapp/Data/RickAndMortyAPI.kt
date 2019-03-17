package com.example.lpiem.rickandmortyapp.Data


import com.example.lpiem.rickandmortyapp.Model.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface RickAndMortyAPI {

    // START HEROKU

    @GET("/")
    fun herokuAwaking(
    ): Call<Void>


    ///CONNEXION - INSCRIPTION

    @POST("/auth/login")
    fun connectUser(
            @Body body: JsonObject
    ): Call<UserResponse>

    @POST("/users/")
    fun signInUser(
            @Body body: JsonObject
    ): Call<UserResponse>

    @POST("/auth/lostpassword")
    fun sendCodeForPassword(
            @Body body: JsonObject
    ): Call<UserResponse>

    @POST("/auth/loginwithcode")
    fun loginWithCode(@Body body: JsonObject
    ): Call<UserResponse>

    ///USER


    @GET("/users/{id}")
    fun getUserById(
            @Path("id") userId: Int
    ): Call<UserResponse>

    @PUT("/users/{id}/password")
    fun changePassword(
            @Path("id") userId: Int,
            @Body body: JsonObject
    ): Call<UserResponse>

    ///KAAMELOTT GAME

    @PUT("/users/playGame/{id}")
    fun putNewDate(
            @Path("id") userId: Int, @Body body: JsonObject
    ): Call<UserResponse>

    @GET("/kaamelott/randomQuote")
    fun getRandomQuote(
    ): Call<KaamlottQuote>


    ///WALLET

    @PUT("/users/wallet/{id}")
    fun updateWallet(
            @Path("id") userId: Int, @Body body: JsonObject
    ): Call<UserResponse>

    @GET("/users/wallet/{id}")
    fun getWallet(
            @Path("id") userId: Int
    ): Call<Wallet>


    ///CARD

    @GET("/cards/randomDeckGenerator/{id}")
    fun getRandomDeck(
            @Path("id") userId: Int
    ): Call<ListOfCards>

    @GET("/users/deck/{id}")
    fun getListOfCardsById(
            @Path("id") userId: Int
    ): Call<ListOfCards>

    @GET("/cards/{id}")
    fun getCardDetails(
            @Path("id") userId: Int
    ): Call<DetailledCard>

    @POST("/cards/addDecks")
    fun increaseNumberOfDecks(
            @Body body: JsonObject
    ): Call<UserResponse>


    ///FRIEND

    @GET("users/{user}/friends/")
    fun getListOfFriends(
            @Path("user") userId: Int
    ): Call<ListOfFriends>

    @GET("users/{user}/friends/search/{new_friends}")
    fun searchForFriends(
            @Path("user") userId: Int,
            @Path("new_friends") newFriends: String?
    ): Call<ListOfFriends>

    @POST("users/{user}/friends/{id2}")
    fun addAFriend(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int
    ): Call<UserResponse>

    @DELETE("users/{user}/friends/{id2}")
    fun deleteAFriend(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int
    ): Call<UserResponse>

    @PUT("users/{user}/friends/{id2}")
    fun validateAFriend(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int
    ): Call<UserResponse>


    ///SETTINGS

    @GET("/faq/")
    fun getFAQ(
    ): Call<ListOfFAQ>


    ///MARKET

    @GET("/users/{id}/market")
    fun getUserMarket(
            @Path("id") userId: Int
    ): Call<ListOfCards>

    @GET("/users/{id}/market/{friend}")
    fun getFriendMarket(
            @Path("id") userId: Int,
            @Path("friend") friend: Int
    ): Call<ListOfCards>

    @POST("/users/{id}/market/{cardId}")
    fun addCardToMarket(
            @Path("id") userId: Int, @Path("cardId") cardId: Int, @Body body: JsonObject
    ): Call<ListOfCards>

    @POST("/users/{id}/market/{friend}/buycard/{card_id}")
    fun buyCardFromFriend(
            @Path("id") userId: Int,
            @Path("friend") friend: Int,
            @Path("card_id") card_id: Int,
            @Body body: JsonObject
    ): Call<UserResponse>


    ///MEMORY GAME

    @GET("cards/randomSelectionFor/{amount}")
    fun getCardSelection(
            @Path("amount") amount: Int
    ): Call<ListOfCards>

    @PUT("/users/playMemory/{id}")
    fun putNewMemoryDate(
            @Path("id") userId: Int, @Body body: JsonObject
    ): Call<UserResponse>

    @POST("/cards/addRewards")
    fun addRewards(
            @Body body: JsonObject
    ): Call<UserResponse>
}
