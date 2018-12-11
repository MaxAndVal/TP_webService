package com.example.lpiem.rickandmortyapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.lpiem.rickandmortyapp.R.id.btn_generate_deck
import com.facebook.AccessToken
import com.facebook.login.LoginBehavior

import com.facebook.login.LoginManager

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayActivity : AppCompatActivity() {

    private lateinit var tv: TextView
    private var message: String? = null
    private var nextPage = 1
    private var nbPages = 25
    private var rickAndMortyAPI: RickAndMortyAPI? = null
    private val listName = ArrayList<String>()
    private lateinit var btnRandomDeck: Button

    private var adapter: ArrayAdapter<String>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        btnRandomDeck = findViewById(R.id.btn_generate_deck)
        btnRandomDeck.setOnClickListener { generateDeck() }

        tv = findViewById(R.id.textView)
        val listView = findViewById<ListView>(R.id.listView)
        adapter = ArrayAdapter(this@DisplayActivity, android.R.layout.simple_list_item_1, listName)
        listView.adapter = adapter

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        callWS()//TODO: uncomment after

    }

    fun <T> callRetrofit(call: Call<T>, i: Int) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if (i == 1) {
                        val result = response.body() as Result // use the user object for the other fields
                        fetchData(response as Response<Result>)
                        if (nextPage < nbPages) {
                            getAllCharacter()
                        }
                    } else if (i == 2) {
                        val character = response.body() as List<Character>
                        Log.d(TAG, "test character = $character")
                    } else if(i == 3) {
                        val responseFromApi = response.body() as ResponseFromApi
                    }
                } else {
                    Log.d("TAG", "error : " + response.errorBody()!!)

                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "$t")
            }
        })

    }

    private fun generateDeck() {
        val resultDecks = rickAndMortyAPI!!.getRandomDeck(19)
        callRetrofit(resultDecks, 2)
    }

    private fun callWS() {
        getAllCharacter()
    }

    private fun getAllCharacter() {
        val resultCall = rickAndMortyAPI!!.getAllCharacters(nextPage)
        callRetrofit(resultCall, 1)
    }


    /*override fun onResponse(call: Call<Any>, response: Response<Any>) {
        if (response.isSuccessful) {
            fetchData(response)
            if (nextPage < nbPages) {
                getAllCharacter()
            }
        } else {
            Log.d("SwapiRetrofitController", "error : " + response.errorBody()!!)
        }
    }

    override fun onFailure(call: Call<Any>, t: Throwable) {
        t.printStackTrace()
    }*/


    @Synchronized
    private fun fetchData(response: Response<Result>) {
        val result = response.body()
        val info = result!!.info
        if (info!!.next != null)
            nextPage++
        else
            nbPages = nextPage

        val listPeople = result!!.results
        message += "list people : \n\n"
        for (character in listPeople!!) {
            Log.d("SwapiRetrofitController", "people name : " + character.name)
            character.name?.let { listName.add(it) }
        }
        adapter?.notifyDataSetChanged()
    }



    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //moveTaskToBack(true)
        //LoginManager.getInstance().logOut()
    }
}
