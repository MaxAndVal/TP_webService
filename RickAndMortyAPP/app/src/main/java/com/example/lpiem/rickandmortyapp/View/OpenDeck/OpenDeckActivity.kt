package com.example.lpiem.rickandmortyapp.View.OpenDeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.OpenDeckManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CollectionDetailActivity
import kotlinx.android.synthetic.main.activity_open_deck.*

class OpenDeckActivity : AppCompatActivity(), OpenDecksInterface {


    private var openDeckManager : OpenDeckManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)
        openDeckManager = OpenDeckManager.getInstance(this)
        iv_closeOpenDeck.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        openDeckManager?.showDetails = true
        tv_openYourDeck.text = String.format(getString(R.string.number_of_deck_to_open), openDeckManager?.loginAppManager?.connectedUser?.deckToOpen)
        iv_peaceAmongWorld.setOnClickListener { openDeckManager!!.openRandomDeck(openDeckManager!!.loginAppManager.connectedUser!!.deckToOpen, this) }
    }

    override fun showAnimation(show: Boolean) {
        if (show) {
            fl_DeckToOpen.visibility = View.GONE
            tv_openYourDeck.setOnClickListener { }
            fl_animation.visibility = View.VISIBLE
            av_from_code.setAnimation("portal_loop.json")
            av_from_code.playAnimation()
            av_from_code.loop(true)
        } else {
            av_from_code.pauseAnimation()
            fl_animation.visibility = View.GONE
            fl_DeckToOpen.visibility = View.VISIBLE
        }
    }


    fun getInfoNewCards() {
        val detailIntent = Intent(this, CollectionDetailActivity::class.java)
        startActivity(detailIntent)
    }

    override fun updateDecksCount(newCount: Int) {
        tv_openYourDeck.text = String.format(getString(R.string.number_of_deck_to_open), newCount)

    }

    override fun onBackPressed() {
        openDeckManager?.showDetails = false
        av_from_code.cancelAnimation()
        super.onBackPressed()
    }

}
