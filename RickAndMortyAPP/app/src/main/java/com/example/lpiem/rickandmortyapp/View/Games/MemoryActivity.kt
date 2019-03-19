package com.example.lpiem.rickandmortyapp.View.Games

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.Home.MemoryGameManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.Games.MemoryReward
import com.example.lpiem.rickandmortyapp.Model.Games.Tile
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_memory.*


class MemoryActivity : AppCompatActivity() {

    private val memoryGameManager = MemoryGameManager(this)
    private val loginAppManager = LoginAppManager.getInstance(this)
    private lateinit var turnObserver: Observer<Int>
    private lateinit var scoreObserver: Observer<Int>
    private lateinit var initListener: Observer<MutableList<Tile>>
    private lateinit var startGameObserver: Observer<Unit>
    private lateinit var drawObserver: Observer<Pair<MutableList<Drawable?>, ListOfCards>>
    private var lisOfImageView: List<ImageView> = ArrayList()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        user = loginAppManager.connectedUser

        declareAllObservers()
        triggerLiveData()

        // init game counters
        tv_turn.text = String.format(getString(R.string.turn_lefts), memoryGameManager.turn)
        tv_memory_score.text = String.format(getString(R.string.current_score), memoryGameManager.score)

        lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)

        memoryGameManager.gameAvailable(user!!)
    }

    private fun setAnimationListener(listOfTiles: MutableList<Tile>) {
        tv_turn.visibility = VISIBLE
        tv_memory_score.visibility = VISIBLE
        tv_memory_title.visibility = VISIBLE
        tv_loading.visibility = GONE
        listOfTiles.forEach { tile ->
            val view = tile.tileView
            view.visibility = VISIBLE

            val image = tile.image
            val placeHolder = tile.placeHolder
            view.setOnClickListener {
                val handler = Handler()

                if (!tile.tapped) {
                    memoryGameManager.setRealImage(view, placeHolder, handler, image, tile, true)
                    tile.tapped = !tile.tapped
                } else {
                    memoryGameManager.setPlaceHolder(view, placeHolder, handler, image, tile, true)
                    tile.tapped = !tile.tapped
                }
            }
        }
    }

    private fun declareAllObservers() {

        drawObserver = Observer { drawAndCardList ->
            for ((i, draw) in drawAndCardList.first.withIndex()) {
                if (draw == null) {
                    Toast.makeText(this, getString(R.string.error_fetching_pictures),
                            Toast.LENGTH_LONG).show()
                    break
                } else {
                    memoryGameManager.list.add(Triple(draw, drawAndCardList.second.cards!![i].cardName!!, drawAndCardList.second.cards!![i].cardId!!))
                    if (memoryGameManager.list.size == 6) {
                        memoryGameManager.initGame(lisOfImageView, memoryGameManager.list)
                        break
                    }
                }
            }
        }

        turnObserver = Observer { currentTurn ->
            tv_turn.text = String.format(getString(R.string.turn_lefts), currentTurn)
        }

        scoreObserver = Observer { currentScore ->
            tv_memory_score.text = String.format(getString(R.string.current_score), currentScore)
        }

        initListener = Observer { list ->
            setAnimationListener(list)
        }

        startGameObserver = Observer {
            if (loginAppManager.memoryInProgress) {
                memoryGameManager.initCardList(6)
            } else {
                tv_loading.text = getString(R.string.try_an_other_game_later)
            }
        }
    }

    private fun triggerLiveData() {

        memoryGameManager.rewardsReceiver.observeOnce(Observer { rewards ->
            if (loginAppManager.memoryInProgress && memoryGameManager.turn == 0) {
                val handler = Handler()
                handler.postDelayed({
                    tv_loading.text = String.format(getString(R.string.memory_game_over),
                            memoryGameManager.score,
                            rewards.toFormattedString())
                    tv_loading.visibility = VISIBLE
                    lisOfImageView.forEach { it.visibility = GONE }
                    tv_memory_score.visibility = GONE
                    tv_memory_title.visibility = GONE
                    tv_turn.visibility = GONE
                }, 1000L)

                loginAppManager.memoryInProgress = false
                memoryGameManager.turn = 8
            }
        })

        memoryGameManager.drawableListReceiver.observeForever(drawObserver)
        memoryGameManager.displayNewTurn.observeForever(turnObserver)
        memoryGameManager.displayNewScore.observeForever(scoreObserver)
        memoryGameManager.finalViewListeners.observeForever(initListener)
        memoryGameManager.startTheGame.observeForever(startGameObserver)
    }

    private fun removeAllObservers() {
        memoryGameManager.displayNewTurn.removeObserver(turnObserver)
        memoryGameManager.displayNewScore.removeObserver(scoreObserver)
        memoryGameManager.finalViewListeners.removeObserver(initListener)
        memoryGameManager.startTheGame.removeObserver(startGameObserver)
        memoryGameManager.drawableListReceiver.removeObserver(drawObserver)
    }

    // MutableList<MemoryReward> extension
    private fun MutableList<MemoryReward>.toFormattedString(): String {
        val result = StringBuilder()
        result.append(getString(R.string.cards_won))
        for (item in this) {
            result.append(item.cardName)
            result.append("\n")
        }
        return result.toString()
    }

    override fun onBackPressed() {
        removeAllObservers()
        loginAppManager.memoryInProgress = true
        memoryGameManager.cancelCall()
        super.onBackPressed()
    }

}




