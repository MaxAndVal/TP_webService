package com.example.lpiem.rickandmortyapp.View.BackActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CollectionDetailActivity
import com.example.lpiem.rickandmortyapp.ViewModel.BackActivity.OpenDeckManager
import kotlinx.android.synthetic.main.activity_open_deck.*

class OpenDeckActivity : AppCompatActivity() {

    private var openDeckManager = OpenDeckManager.getInstance(this)
    private lateinit var updateDeckCountObserver: Observer<Int>
    private lateinit var newCardObserver: Observer<Int>

    companion object {
        lateinit var obs: Observer<Int>
        fun setObserver(observer: Observer<Int>) {
            obs = observer
        }
        fun getObserver() : Observer<Int> {
            return obs
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_deck)

        updateDeckCountObserver = Observer {
            updateDecksCount(it)
        }

        newCardObserver = Observer {
            if (openDeckManager.showDetails) {
                getInfoNewCards(it)
                openDeckManager.showDetails = false
            }
        }

        openDeckManager.updateDeckCountLiveData.observeForever(updateDeckCountObserver)

    }

    override fun onResume() {
        super.onResume()

        while (openDeckManager.infoNewCardLiveData.hasObservers()) {
            openDeckManager.infoNewCardLiveData.removeObserver(newCardObserver)
        }

        iv_closeOpenDeck.setOnClickListener { onBackPressed() }
        tv_openYourDeck.text = String.format(
                getString(R.string.number_of_deck_to_open),
                openDeckManager.loginAppManager.connectedUser?.deckToOpen
        )
        iv_peaceAmongWorld.setOnClickListener {
            if (openDeckManager.loginAppManager.connectedUser!!.deckToOpen!! > 0) {
                showAnimation(true)
                openDeckManager.infoNewCardLiveData.observeForever(newCardObserver)
                OpenDeckActivity.setObserver(newCardObserver)
                openDeckManager.openRandomDeck(openDeckManager.loginAppManager.connectedUser!!.deckToOpen)
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

    private fun getInfoNewCards(deckLeft: Int) {
        showAnimation(false)
        val detailIntent = Intent(this, CollectionDetailActivity::class.java)
        startActivity(detailIntent)
        if (deckLeft == 1) finish()
    }

    private fun updateDecksCount(newCount: Int) {
        tv_openYourDeck.text = String.format(getString(R.string.number_of_deck_to_open), newCount)
    }

    override fun onBackPressed() {
        openDeckManager.updateDeckCountLiveData.removeObserver(updateDeckCountObserver)
        openDeckManager.showDetails = false
        av_from_code.cancelAnimation()
        openDeckManager.cancelCall()
        super.onBackPressed()
    }

}
