package com.example.lpiem.rickandmortyapp

import com.google.gson.GsonBuilder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RickAndMortyRetrofitSingleton {

    private const val BASE_URL = "https://rickandmortyapi.com/"

    private var rickAndMortyAPIInstance: RickAndMortyAPI? = null

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
}
