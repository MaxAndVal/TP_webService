package com.example.lpiem.rickandmortyapp.View.OpenDeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.example.lpiem.rickandmortyapp.Manager.OpenDeckManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CollectionDetailActivity
import kotlinx.android.synthetic.main.activity_open_deck.*

class OpenDeckActivity : AppCompatActivity() {

    private var openDeckManager = OpenDeckManager.getInstance(this)
    private var animationLiveData = MutableLiveData<Boolean>()
    private var updateDeckCountLiveData = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)
        iv_closeOpenDeck.setOnClickListener { finish() }

        animationLiveData.observeOnce(Observer {
            showAnimation(it)
        })

        updateDeckCountLiveData.observeOnce(Observer {
            updateDecksCount(it)
        })
    }

    override fun onResume() {
        super.onResume()
        openDeckManager.showDetails = true
        tv_openYourDeck.text = String.format(
                getString(R.string.number_of_deck_to_open),
                openDeckManager.loginAppManager.connectedUser?.deckToOpen
        )
        iv_peaceAmongWorld.setOnClickListener {
            if (openDeckManager.loginAppManager.connectedUser!!.deckToOpen!! > 0) {
                showAnimation(true)
                openDeckManager.openRandomDeck(
                        openDeckManager.loginAppManager.connectedUser!!.deckToOpen,
                        updateDeckCountLiveData
                )
            }
        }
    }

    private fun showAnimation(show: Boolean) {
        when (show) {
            true -> {
                fl_DeckToOpen.visibility = View.GONE
                tv_openYourDeck.setOnClickListener { }
                fl_animation.visibility = View.VISIBLE
                av_from_code.setAnimation("portal_loop.json")
                av_from_code.repeatCount = LottieDrawable.INFINITE
                av_from_code.playAnimation()
            }
            false -> {
                av_from_code.pauseAnimation()
                fl_animation.visibility = View.GONE
                fl_DeckToOpen.visibility = View.VISIBLE
            }
        }
    }

    fun getInfoNewCards() {
        showAnimation(false)
        val detailIntent = Intent(this, CollectionDetailActivity::class.java)
        startActivity(detailIntent)
    }

    private fun updateDecksCount(newCount: Int) {
        tv_openYourDeck.text = String.format(getString(R.string.number_of_deck_to_open), newCount)
    }

    override fun onBackPressed() {
        openDeckManager.showDetails = false
        av_from_code.cancelAnimation()
        super.onBackPressed()
    }

}
