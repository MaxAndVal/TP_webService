package com.example.lpiem.rickandmortyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayActivity extends AppCompatActivity implements Callback<Result> {

    TextView tv;
    String message;
    int nextPage =1, nbPages=25;
    RickAndMortyAPI rickAndMortyAPI;
    private ArrayList<String> listName = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);


        tv = findViewById(R.id.textView);
        ListView listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(DisplayActivity.this, android.R.layout.simple_list_item_1, listName);
        listView.setAdapter(adapter);

        //RaMRetrofitController raMRetrofitController = new RaMRetrofitController();
        //listName = raMRetrofitController.callWS();

        callWS();

    }

    public void callWS() {

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance();
        getNextListCharacter();

        Call<Character> characterCall = rickAndMortyAPI.getCharacterById(1);
        characterCall.enqueue(new Callback<Character>() {
            @Override
            public void onResponse(Call<Character> call, Response<Character> response) {
                if(response.isSuccessful()){
                    Character character = response.body();
                    Log.d("test", "onResponse: " + character);
                    message = character.getName();
                    tv.setText(message);
                }

            }

            @Override
            public void onFailure(Call<Character> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getNextListCharacter(){
        Call<Result> resultCall = rickAndMortyAPI.getListCharacter(nextPage);
        resultCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<Result> call, Response<Result> response) {
        if (response.isSuccessful()) {
            fetchData(response);
            if (nextPage < nbPages) {
                getNextListCharacter();
            }
        } else {
            Log.d("SwapiRetrofitController", "error : " + response.errorBody());
        }
    }
    private synchronized void fetchData(Response<Result> response) {
        Result result = response.body();
        Info info = result.getInfo();
        if (info.getNext() != null) nextPage++;
        else nbPages = nextPage;

        List<Character> listPeople = result.getResults();
        message += "list people : \n\n";
        for (Character character : listPeople) {
            Log.d("SwapiRetrofitController", "people name : " + character.getName());
            listName.add(character.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<Result> call, Throwable t) {
        t.printStackTrace();
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginManager.getInstance().logOut();
    }
}
