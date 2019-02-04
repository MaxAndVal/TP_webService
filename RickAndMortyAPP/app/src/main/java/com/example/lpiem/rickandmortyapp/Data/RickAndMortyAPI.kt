package com.example.lpiem.rickandmortyapp.Data


import com.example.lpiem.rickandmortyapp.Model.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface RickAndMortyAPI {

    @GET("/api/character/{id}")
    fun getCharacterById(
            @Path("id") id: Int
    ): Call<Character>

    @GET("/api/character/")
    fun getListCharacter(
            @Query("page") page: Int
    ): Call<Result>

    @GET("/cards/all/{page}")
    fun getAllCharacters(
            @Path("page") page: Int
    ): Call<Result>

    @POST("/auth/login")
    fun connectUser(
            @Body body: JsonObject
    ): Call<ResponseFromApi>

    @POST("/users/")
    fun signinUser(
            @Body body: JsonObject
    ): Call<ResponseFromApi>

    @GET("/users/{id}")
    fun getUserById(
            @Path("id") userId: Int
    ): Call<ResponseFromApi>

    @PUT("/users/playGame/{id}")
    fun putNewDate(
            @Path("id") userId: Int, @Body body: JsonObject
    ): Call<ResponseFromApi>

    @PUT("/users/wallet/{id}")
    fun updateWallet(
            @Path("id") userId: Int, @Body body: JsonObject
    ): Call<ResponseFromApi>

    @GET("/users/wallet/{id}")
    fun getWallet(
            @Path("id") userId: Int
    ): Call<Wallet>

    @GET("/cards/randomDeckGenerator/{id}")
    fun getRandomDeck(
            @Path("id") userId: Int
    ): Call<List<Character>>

    @GET("/users/deck/{id}")
    fun getListOfCardsById(
            @Path("id") userId: Int
    ): Call<ListOfCards>

    @GET("/kaamelott/randomQuote")
    fun getRamdomQuote(
    ): Call<KaamlottQuote>

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
    fun addAfriends(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int): Call<ResponseFromApi>

    @DELETE("users/{user}/friends/{id2}")
    fun delAfriends(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int): Call<ResponseFromApi>

    @PUT("users/{user}/friends/{id2}")
    fun valideAFriends(
            @Path("user") userId1: Int,
            @Path("id2") userId2: Int): Call<ResponseFromApi>

    @GET("/cards/{id}")
    fun getCardDetails(
            @Path("id") userId: Int
    ): Call<DetailledCard>

    @POST("/cards/addDecks")
    fun increaseNumberOfDecks(
            @Body body: JsonObject
    ): Call<ResponseFromApi>
}
