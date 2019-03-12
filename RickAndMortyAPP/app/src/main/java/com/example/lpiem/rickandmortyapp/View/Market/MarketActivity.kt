package com.example.lpiem.rickandmortyapp.View.Market

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.Market.MarketManager
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.RecyclerTouchListener
import com.example.lpiem.rickandmortyapp.View.Collection.list.CardListDisplay
import kotlinx.android.synthetic.main.activity_market.*

class MarketActivity : AppCompatActivity(), CardListDisplay {

    var listOfCards: ListOfCards? = null
    private lateinit var marketManager: MarketManager
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
    private var adapter: MarketAdapter? = null
    private var friendId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)
        if (intent.hasExtra("friend_id")) {
            friendId = intent.extras["friend_id"] as Int
        }

        loginAppManager = LoginAppManager.getInstance(this)
        user = loginAppManager.connectedUser
        Log.d(com.example.lpiem.rickandmortyapp.View.TAG, "user : $user")

        marketManager = MarketManager.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        rv_market.layoutManager = GridLayoutManager(this, 2)
        marketManager.getMarket(user, this, friendId)

        rv_market.addOnItemTouchListener(RecyclerTouchListener(this, rv_market, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val card = (rv_market.adapter as MarketAdapter).getDataSet().cards?.get(position)
                if (card!!.price!! < user!!.userWallet!!) {
                    marketManager.buyCard(card, user!!.userId, friendId)
                    //marketManager.getMarket(user, this@MarketActivity, friendId)
                    //marketManager.rickAndMortyAPI.updateUserInfo(user!!.userId)

                } else {
                    Toast.makeText(applicationContext, "Too poor to buy anything... get a job", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }))

    }

    private fun updateAdapter(list: ListOfCards) {
        if (adapter == null) {
            adapter = MarketAdapter(list)
            rv_market.adapter = adapter
            adapter!!.notifyDataSetChanged()
        } else {
            adapter!!.dataSet = list
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun displayResult(list: ListOfCards) {
        updateAdapter(list)
    }

    override fun onBackPressed() {
        marketManager.cancelCall()
        super.onBackPressed()
    }
}
