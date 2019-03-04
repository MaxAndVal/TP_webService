package com.example.lpiem.rickandmortyapp.View.Memory

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.MemoryGameManager
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_memory.*


class MemoryActivity : AppCompatActivity() {

    private val memoryGameManager = MemoryGameManager.getInstance(this)
    private lateinit var turnObserver: Observer<Int>
    private lateinit var scoreObserver: Observer<Int>
    private lateinit var initListeners : Observer<MutableList<Tile>>
    private var displayTurn = MutableLiveData<Int>()
    private var displayScore = MutableLiveData<Int>()
    private var imageViewsToListen = MutableLiveData<MutableList<Tile>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        memoryGameManager.activity = this

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

        val lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)

        memoryGameManager.initCardList(6, lisOfImageView, imageViewsToListen)

    }

    private fun setAnimationListener(listOfTiles: MutableList<Tile>) {
        for (tile in listOfTiles) {
            val view = tile.tileView

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

    override fun onBackPressed() {
        memoryGameManager.displayNewTurn.removeObserver(turnObserver)
        memoryGameManager.displayNewScore.removeObserver(scoreObserver)
        imageViewsToListen.removeObserver(initListeners)
        super.onBackPressed()
    }

}




