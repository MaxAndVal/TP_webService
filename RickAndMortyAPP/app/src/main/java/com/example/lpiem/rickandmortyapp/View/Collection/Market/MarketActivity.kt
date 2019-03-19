package com.example.lpiem.rickandmortyapp.View.Collection.Market

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.Market.MarketManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_market.*

class MarketActivity : AppCompatActivity() {

    var listOfCards: ListOfCards? = null
    private lateinit var marketManager: MarketManager
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
    private var adapter: MarketAdapter? = null
    private var friendId: Int? = null
    private lateinit var displayObserver: Observer<ListOfCards>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)
        if (intent.hasExtra("friend_id")) {
            friendId = intent.extras["friend_id"] as Int
        }

        loginAppManager = LoginAppManager.getInstance(this)
        user = loginAppManager.connectedUser
        Log.d(com.example.lpiem.rickandmortyapp.View.Connection.TAG, "user : $user")

        marketManager = MarketManager.getInstance(this)

        displayObserver = Observer {list ->
            if (adapter == null) {
                adapter = MarketAdapter(list)
                rv_market.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else {
                adapter!!.dataSet = list
                adapter!!.notifyDataSetChanged()
            }
        }
        marketManager.cardListDisplay.observeForever(displayObserver)
    }

    override fun onResume() {
        super.onResume()
        rv_market.layoutManager = GridLayoutManager(this, 2)
        marketManager.getMarket(user, friendId)

        rv_market.addOnItemTouchListener(RecyclerTouchListener(this, rv_market, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {
                val card = (rv_market.adapter as MarketAdapter).getDataSet().cards?.get(position)
                if (card!!.price!! < user!!.userWallet!!) {
                    marketManager.buyCard(card, user!!.userId, friendId)
                    marketManager.rickAndMortyAPI.updateUserInfo(user!!.userId)

                } else {
                    Toast.makeText(applicationContext, getString(R.string.too_poor_to_buy), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }))

    }

    override fun onBackPressed() {
        marketManager.cancelCall()
        marketManager.cardListDisplay.removeObserver(displayObserver)
        super.onBackPressed()
    }
}
