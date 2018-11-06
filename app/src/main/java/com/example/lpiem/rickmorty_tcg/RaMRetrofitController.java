package com.example.lpiem.rickmorty_tcg;

import android.util.Log;

import com.example.lpiem.rickmorty_tcg.model.Character;
import com.example.lpiem.rickmorty_tcg.model.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaMRetrofitController {

    public void start() {

        RickAndMortyAPI rickAndMortyAPI = RamapiRetrofitSingleton.getInstance();

        Call<Character> characterCall = rickAndMortyAPI.getCharacterById(1);

        characterCall.enqueue(new Callback<Character>() {
            @Override
            public void onResponse(Call<Character> call, Response<Character> response) {
                Character character = response.body();
                Log.d("Retro", "onResponse: " + character.getName());
            }

            @Override
            public void onFailure(Call<Character> call, Throwable t) {
                Log.d("Retro", "onFailure: " + t.getMessage());
            }
        });

        Call<Result> resultCall = rickAndMortyAPI.getListCharacter(1);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
