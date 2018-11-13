package com.example.lpiem.rickandmortyapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RickAndMortyRetrofitSingleton {

    static final String BASE_URL = "https://rickandmortyapi.com/";

    private static RickAndMortyAPI rickAndMortyAPIInstance;

    public static RickAndMortyAPI getInstance(){
        if(rickAndMortyAPIInstance == null)
            synchronized (RickAndMortyAPI.class){
            createApiBuilder();
            }
            return rickAndMortyAPIInstance;
    }

    private static void createApiBuilder(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        rickAndMortyAPIInstance = retrofit.create(RickAndMortyAPI.class);
    }
}
