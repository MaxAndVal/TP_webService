package com.example.lpiem.rickandmortyapp.View.Memory

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.MemoryGameManager
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_memory.*


class MemoryActivity : AppCompatActivity() {

    private val memoryGameManager = MemoryGameManager.getInstance(this)
    private val loginAppManager = LoginAppManager.getInstance(this)
    private lateinit var turnObserver: Observer<Int>
    private lateinit var scoreObserver: Observer<Int>
    private lateinit var initListener: Observer<MutableList<Tile>>
    private lateinit var startGameObserver: Observer<Unit>
    private lateinit var drawObserver: Observer<Pair<MutableList<Drawable?>, ListOfCards>>
    private var finalTilesListener = MutableLiveData<MutableList<Tile>>()
    private var lisOfImageView: List<ImageView> = ArrayList()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        user = loginAppManager.connectedUser

        memoryGameManager.rewardsReceiver.observeOnce(Observer { rewards ->
            if (loginAppManager.memoryInProgress) {
                Toast.makeText(this,
                        String.format(getString(R.string.memory_game_over),
                                memoryGameManager.score,
                                rewards.toFormattedString()),
                        Toast.LENGTH_LONG).show()
                loginAppManager.memoryInProgress = false
            }
        })

        drawObserver = Observer { drawAndCardList ->
            for ((i, draw) in drawAndCardList.first.withIndex()) {
                if (draw == null) {
                    Toast.makeText(this, getString(R.string.error_fetching_pictures),
                            Toast.LENGTH_LONG).show()
                    break
                } else {
                    memoryGameManager.list.add(Pair(draw, drawAndCardList.second.cards!![i].cardName!!))
                    if (memoryGameManager.list.size == 6) {
                        memoryGameManager.initGame(lisOfImageView, memoryGameManager.list)
                        break
                    }
                }
            }
        }
        memoryGameManager.drawableListReceiver.observeForever(drawObserver)

        turnObserver = Observer { currentTurn ->
            tv_turn.text = String.format(getString(R.string.turn_lefts), currentTurn)
        }

        scoreObserver = Observer { currentScore ->
            tv_memory_score.text = String.format(getString(R.string.current_score), currentScore)
        }

        initListener = Observer { list ->
            setAnimationListener(list)
        }

        memoryGameManager.displayNewTurn.observeForever(turnObserver)
        memoryGameManager.displayNewScore.observeForever(scoreObserver)
        finalTilesListener.observeForever(initListener)

        // init game counters
        tv_turn.text = String.format(getString(R.string.turn_lefts), memoryGameManager.turn)
        tv_memory_score.text = String.format(getString(R.string.current_score), memoryGameManager.score)

        lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)

        startGameObserver = Observer {
            if (loginAppManager.memoryInProgress) {
                memoryGameManager.initCardList(6, finalTilesListener)
            } else {
                tv_loading.text = getString(R.string.try_an_other_game_later)
            }
        }

        memoryGameManager.startTheGame.observeForever(startGameObserver)
        memoryGameManager.gameAvailable(user!!)
    }

    private fun setAnimationListener(listOfTiles: MutableList<Tile>) {
        tv_turn.visibility = VISIBLE
        tv_memory_score.visibility = VISIBLE
        tv_memory_title.visibility = VISIBLE
        tv_loading.visibility = GONE
        for (tile in listOfTiles) {
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


    // MutableList<String> extension
    private fun MutableList<String>.toFormattedString(): String {
        val result = StringBuilder()
        result.append(getString(R.string.cards_won))
        for (item in this) {
            result.append(item)
            result.append("\n")
        }
        return result.toString()
    }

    override fun onBackPressed() {
        memoryGameManager.displayNewTurn.removeObserver(turnObserver)
        memoryGameManager.displayNewScore.removeObserver(scoreObserver)
        finalTilesListener.removeObserver(initListener)
        memoryGameManager.startTheGame.removeObserver(startGameObserver)
        memoryGameManager.drawableListReceiver.removeObserver(drawObserver)
        loginAppManager.memoryInProgress = true
        memoryGameManager.cancelCall()
        super.onBackPressed()
    }

}




