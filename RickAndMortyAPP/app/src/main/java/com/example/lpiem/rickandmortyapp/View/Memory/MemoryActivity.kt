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
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_memory.*


class MemoryActivity : AppCompatActivity() {

    private val memoryGameManager = MemoryGameManager.getInstance(this)
    private val loginAppManager = LoginAppManager.getInstance(this)
    private lateinit var turnObserver: Observer<Int>
    private lateinit var scoreObserver: Observer<Int>
    private lateinit var initListeners: Observer<MutableList<Tile>>
    private var displayTurn = MutableLiveData<Int>()
    private var displayScore = MutableLiveData<Int>()
    private var imageViewsToListen = MutableLiveData<MutableList<Tile>>()
    private var rewardsLiveData = MutableLiveData<MutableList<String>>()
    private var drawLiveData = MutableLiveData<Pair<MutableList<Drawable?>, ListOfCards>>()
    private lateinit var lisOfImageView: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        /**TODO:
        add web put to add date for memory game AND test
        here for it before doing anything or changing text for tv_loading in
        "Vous avez déjà joué aujourd'hui !!" */

        rewardsLiveData.observeOnce(Observer { rewards ->
            Toast.makeText(this,
                    String.format(getString(R.string.memory_game_over),
                            memoryGameManager.score,
                            rewards.toFormattedString()),
                    Toast.LENGTH_LONG).show()
        })

        drawLiveData.observeOnce(Observer {
                for ((i, draw) in it.first.withIndex()) {
                    if (draw == null) {
                        Toast.makeText(this, getString(R.string.error_fetching_pictures),
                                Toast.LENGTH_LONG).show()
                        memoryGameManager.interruptThread()
                        break
                    } else {
                        memoryGameManager.list.add(Pair(draw, it.second.cards!![i].cardName!!))
                        if (memoryGameManager.list.size == 6) {
                            memoryGameManager.initGame(lisOfImageView, imageViewsToListen, memoryGameManager.list)
                            memoryGameManager.interruptThread()
                            break
                        }
                    }
                }
        })

        turnObserver = Observer { currentTurn ->
            tv_turn.text = String.format(getString(R.string.turn_lefts), currentTurn)
        }

        scoreObserver = Observer { currentScore ->
            tv_memory_score.text = String.format(getString(R.string.current_score), currentScore)
        }

        initListeners = Observer { list ->
            setAnimationListener(list)
        }

        memoryGameManager.displayNewScore = displayScore
        memoryGameManager.displayNewTurn = displayTurn
        memoryGameManager.displayNewTurn.observeForever(turnObserver)
        memoryGameManager.displayNewScore.observeForever(scoreObserver)
        imageViewsToListen.observeForever(initListeners)

        // init game counters
        tv_turn.text = String.format(getString(R.string.turn_lefts), memoryGameManager.turn)
        tv_memory_score.text = String.format(getString(R.string.current_score), memoryGameManager.score)

        lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)

        memoryGameManager.initCardList(6, lisOfImageView, imageViewsToListen, rewardsLiveData, drawLiveData)
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
        imageViewsToListen.removeObserver(initListeners)
        super.onBackPressed()
    }

}




