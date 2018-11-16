package com.example.lpiem.rickandmortyapp


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RickAndMortyAPI {

    @GET("/api/character/{id}")
    fun getCharacterById(
            @Path("id") id: Int
    ): Call<Character>

    @GET("/api/character/")
    fun getListCharacter(
            @Query("page") page: Int
    ): Call<Result>

}
