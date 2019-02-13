package com.example.lpiem.rickandmortyapp.View.Market

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.Presenter.Market.MarketManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_market.*
import kotlinx.android.synthetic.main.activity_splash_screen.*

class MarketActivity : AppCompatActivity(), CardListDisplay {

    private var rickAndMortyAPI: RickAndMortyAPI? = null
    var listOfCards: ListOfCards? = null
    private lateinit var marketManager: MarketManager
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
    private var adapter: MarketAdapter? = null
    private var friend_id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)
        if (intent.hasExtra("friend_id")) {
            friend_id = intent.extras["friend_id"] as Int
        }

        loginAppManager = LoginAppManager.getInstance(this)
        user = loginAppManager.connectedUser
        Log.d(com.example.lpiem.rickandmortyapp.View.TAG, "user : $user")

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        marketManager = MarketManager.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        rv_market.layoutManager = GridLayoutManager(this, 2)
        marketManager.getMarket(user, this, friend_id)
    }

    private fun updateAdapter(list: ListOfCards) {
        if (adapter == null) {
            adapter = MarketAdapter(list)
            rv_market.adapter = adapter
            adapter!!.updateList(list)
        } else {
            adapter!!.updateList(list)
        }
    }

    override fun displayResult(list: ListOfCards) {
        updateAdapter(list)
    }
}