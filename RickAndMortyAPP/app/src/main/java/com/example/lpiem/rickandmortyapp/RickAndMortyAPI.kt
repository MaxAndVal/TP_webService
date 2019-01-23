package com.example.lpiem.rickandmortyapp


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

    @GET("/cards/randomDeckGenerator/{id}")
    fun getRandomDeck(
            @Path("id") userId: Int
    ): Call<List<Character>>

    @GET("/users/deck/{id}")
    fun getListOfDecksById(
            @Path("id") userId: Int
    ): Call<ListOfDecks>

    @GET("/api/kaamlott/randomQuote")
    fun getRamdomQuote(
    ): Call<KaamlottQuote>
}
