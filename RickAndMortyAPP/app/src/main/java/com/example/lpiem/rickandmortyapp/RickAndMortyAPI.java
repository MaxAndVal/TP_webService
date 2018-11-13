package com.example.lpiem.rickandmortyapp;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RickAndMortyAPI {

    @GET("/api/character/{id}")
    Call<Character> getCharacterById(
            @Path("id") int id
    );

    @GET("/api/character/")
    Call<Result> getListCharacter(
            @Query("page") int page
    );

}
