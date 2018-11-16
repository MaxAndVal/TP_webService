package com.example.lpiem.rickandmortyapp


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

    @POST("/users/createUser")
    fun createUser(
            @Field("user_name") userName: String,
            @Field("user_email") userEmail: String,
            @Field("user_password") userPassword: String
    ): Call<ResponseFromApi>
}
