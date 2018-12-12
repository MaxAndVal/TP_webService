package com.example.lpiem.rickandmortyapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso

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
    private lateinit var btnDeck: Button
    private lateinit var ivCard: ImageView
    private var adapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        ivCard = findViewById(R.id.iv_card_image)
        btnDeck = findViewById(R.id.btn_deck)
        btnRandomDeck = findViewById(R.id.btn_generate_deck)

        btnDeck.setOnClickListener {
            getListOfDecks()
            val intent = Intent(this, BottomActivity::class.java)
            startActivity(intent)
        }
        btnRandomDeck.setOnClickListener { generateDeck() }

        tv = findViewById(R.id.textView)
        val listView = findViewById<ListView>(R.id.listView)
        adapter = ArrayAdapter(this@DisplayActivity, android.R.layout.simple_list_item_1, listName)
        listView.adapter = adapter

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        callWS()

    }

    private fun <T> callRetrofit(call: Call<T>, i: Int) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if (i == 1) {
                        fetchData(response as Response<Result>)
                        if (nextPage < nbPages) {
                            getAllCharacter()
                        }
                    } else if (i == 2) {
                        val character = response.body() as List<Character>
                        Log.d(TAG, "test character = $character")
                    } else if (i == 3) {
                        val responseFromApi = response.body() as ResponseFromApi
                        Log.d(TAG, "responseFromApi: ${responseFromApi.code} / success: ${responseFromApi.success} / ${responseFromApi.results}")
                    } else if (i == 4) {
                        val listOfDecks = response.body() as ListOfDecks
                        Log.d(TAG, "deck: ${listOfDecks.decks?.get(0)?.cardName} , ${listOfDecks.decks?.get(2)?.cardName}")
                        val image = listOfDecks.decks?.get(0)?.cardImage
                        Picasso.get().load(image).into(ivCard)
                    }
                } else {
                    val responseError = response.errorBody() as ResponseError
                    Log.d(TAG, "error: ${responseError.message}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    private fun getListOfDecks() {
        val resultListDeck = rickAndMortyAPI!!.getListOfDecksById(1)
        callRetrofit(resultListDeck, 4)
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
