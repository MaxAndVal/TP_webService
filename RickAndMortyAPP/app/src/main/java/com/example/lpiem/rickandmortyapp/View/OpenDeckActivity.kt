package com.example.lpiem.rickandmortyapp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Presenter.OpenDeckManager
import com.example.lpiem.rickandmortyapp.R

import kotlinx.android.synthetic.main.activity_open_deck.*

class OpenDeckActivity : AppCompatActivity() {

    var openDeckManager : OpenDeckManager? = null
    var deckOpened : List<Card>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)
        openDeckManager = OpenDeckManager.getInstance(this)
        iv_closeOpenDeck.setOnClickListener { onBackPressed() }

        tv_openYourDeck.text = "You have ${openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen} deck to open"
        iv_peaceAmongWorld.setOnClickListener { openDeckManager!!.openRandomDeck(openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen) }
    }

    override fun onResume() {
        super.onResume()
        tv_openYourDeck.text = "You have ${openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen} deck to open"
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
