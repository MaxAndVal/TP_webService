package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Character
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.Result
import com.example.lpiem.rickandmortyapp.R
import com.facebook.login.LoginManager
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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
    private var userId: Int? =-1


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
                        Log.d(TAG, "test character = ${character[0]}")
                    } else if (i == 3) {
                        val responseFromApi = response.body() as ResponseFromApi
                        Log.d(TAG, "responseFromApi: ${responseFromApi.code} / message: ${responseFromApi.message} / ${responseFromApi.results}")
                    } else if (i == 4) {
                        val listOfDecks = response.body() as ListOfCards
                        val image = listOfDecks.cards?.get(0)?.cardImage
                        Picasso.get().load(image).into(ivCard)
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })
    }

    private fun getListOfDecks() {
        val resultListDeck = rickAndMortyAPI!!.getListOfCardsById(userId?:-1)
        callRetrofit(resultListDeck, 4)
    }

    private fun generateDeck() {
        val resultDecks = rickAndMortyAPI!!.getRandomDeck(userId?:-1)
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

        val listPeople = result.results
        message += "list people : \n\n"
        for (character in listPeople!!) {
            Log.d(TAG, "people name : " + character.name)
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
        LoginManager.getInstance().logOut()
    }
}
