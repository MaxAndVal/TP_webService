package com.example.lpiem.rickandmortyapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.login.LoginBehavior

import com.facebook.login.LoginManager

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayActivity : AppCompatActivity(), Callback<Result> {

    private lateinit var tv: TextView
    private var message: String? = null
    private var nextPage = 1
    private var nbPages = 25
    private var rickAndMortyAPI: RickAndMortyAPI? = null
    private val listName = ArrayList<String>()

    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)


        tv = findViewById(R.id.textView)
        val listView = findViewById<ListView>(R.id.listView)
        adapter = ArrayAdapter(this@DisplayActivity, android.R.layout.simple_list_item_1, listName)
        listView.adapter = adapter

        callWS()//TODO: uncomment after

    }

    private fun callWS() {
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        getAllCharacter()
    }

    private fun getAllCharacter() {
        val resultCall = rickAndMortyAPI!!.getAllCharacters(nextPage)
        resultCall.enqueue(this)
    }


    override fun onResponse(call: Call<Result>, response: Response<Result>) {
        if (response.isSuccessful) {
            fetchData(response)
            if (nextPage < nbPages) {
                getAllCharacter()
            }
        } else {
            Log.d("SwapiRetrofitController", "error : " + response.errorBody()!!)
        }
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

    override fun onFailure(call: Call<Result>, t: Throwable) {
        t.printStackTrace()
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
