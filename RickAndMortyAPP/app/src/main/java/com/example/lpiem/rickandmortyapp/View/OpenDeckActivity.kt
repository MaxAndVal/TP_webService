package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Presenter.OpenDeckManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CollectionDetailActivity
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionAdapter

import kotlinx.android.synthetic.main.activity_open_deck.*
import kotlinx.android.synthetic.main.fragment_collection.*

class OpenDeckActivity : AppCompatActivity() {

    var openDeckManager : OpenDeckManager? = null
    var listOfnewCards : List<Card>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)
        openDeckManager = OpenDeckManager.getInstance(this)
        iv_closeOpenDeck.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        iv_peaceAmongWorld.setOnClickListener { openDeckManager!!.openRandomDeck(openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen) }
        tv_openYourDeck.text = "You have ${openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen} deck to open"
    }


    fun getInfoNewCards(i : Int) {
        val detailIntent = Intent(this, CollectionDetailActivity::class.java)
        Log.d(TAG, "ici " + this!!.listOfnewCards!![i].cardId)
        detailIntent.putExtra("current_card", this!!.listOfnewCards!![i])
        startActivity(detailIntent)
    }
}
