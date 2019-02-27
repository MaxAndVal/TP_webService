package com.example.lpiem.rickandmortyapp.View.Memory

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_memory.*
import java.util.*


class MemoryActivity : AppCompatActivity() {

    private val animationTime = 500L
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = Stack()
    private var score = 0
    private var turn = 8
    private var halfTurn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        tv_turn.text = "Coups restant : $turn"

        val listOfPictures: MutableList<Pair<Int, Int>> = mutableListOf(
                Pair(R.drawable.test1, 1), Pair(R.drawable.test2, 2),
                Pair(R.drawable.test3, 3), Pair(R.drawable.test4, 4),
                Pair(R.drawable.test5, 5), Pair(R.drawable.test6, 6)
        )

        var rand = Math.abs(Random().nextInt()%10) * 2
        listOfPictures.addAll(listOfPictures)
        var finalListOfPictures = listOfPictures.toList()

        while (rand > 0) {
            finalListOfPictures = finalListOfPictures.shuffled()
            rand--
        }


        val lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)



        for (i in 0 until lisOfImageView.size) {
            lisOfImageView[i].tag = finalListOfPictures[i]
            val image = finalListOfPictures[i].first
            val ref = finalListOfPictures[i].second
            val imageView = lisOfImageView[i]
            listOfTiles.add(Tile(image = image,
                    refId = ref,
                    placeHolder = R.drawable.card_back,
                    tapped = false,
                    tileView = imageView)
            )
        }

        setAnimationListener(listOfTiles)

    }

    private fun setAnimationListener(listOfTiles: MutableList<Tile>) {
        for (tile in listOfTiles) {
            val view = tile.tileView

            val image = tile.image
            val placeHolder = tile.placeHolder
            view.setOnClickListener {
                val handler = Handler()

                if (!tile.tapped) {
                    setRealImage(view, placeHolder, handler, image, tile, true)
                    tile.tapped = !tile.tapped

                } else {
                    setPlaceHolder(view, placeHolder, handler, image, tile, true)
                    tile.tapped = !tile.tapped

                }
            }
        }
    }

    private fun setRealImage(view: ImageView, placeHolder: Int, handler: Handler, image: Int, tile: Tile, onChange: Boolean) {
        var drawable: Int
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = placeHolder
        view.setImageResource(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = image
            view.setImageResource(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onChange) clickedElements.onChange(tile)
        }, animationTime)
        tile.tapped = !tile.tapped
    }

    private fun setPlaceHolder(view: ImageView, placeHolder: Int, handler: Handler, image: Int, tile: Tile, onChange: Boolean) {
        var drawable: Int
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = image
        view.setImageResource(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = placeHolder
            view.setImageResource(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onChange) clickedElements.onChange(tile)
        }, animationTime)

    }

    // extension
    private fun MutableList<Tile>.onChange(tile: Tile) {
        // add only if different tile or list empty
        if (halfTurn) {
            turn--
            halfTurn = !halfTurn
        } else {
            halfTurn = true
        }

        tv_turn.text = "Coups restants : $turn"
        val handler = Handler()
        if (this.isEmpty()) {
            this.add(tile)
        } else if (tile != this[0]) {
            this.add(tile)
        }

        if (this.size % 2 == 0 && this.size != 0) {
            val firstTile = this[this.size - 1]
            val secondTile = this[this.size - 2]
            if (firstTile.image != secondTile.image) {
                handler.postDelayed({
                    setPlaceHolder(firstTile.tileView, firstTile.placeHolder, handler, firstTile.image, firstTile, false)
                    setPlaceHolder(secondTile.tileView, secondTile.placeHolder, handler, secondTile.image, secondTile, false)
                    this.clear()
                }, animationTime)
            } else if (firstTile.image == secondTile.image) {
                score++
                tv_memory_score.text = "Score : $score"
                val toast = Toast.makeText(this@MemoryActivity, "Vous avez déjà gagné cette tuille !", Toast.LENGTH_SHORT)

                firstTile.tileView.setOnClickListener {
                    toast.show()
                }
                secondTile.tileView.setOnClickListener {
                    toast.show()
                }



                this.clear()

            }
        }

        if (isGameFinished(turn)) {
            for (item in listOfTiles) {
                item.tileView.setOnClickListener {  }
            }
            Toast.makeText(this@MemoryActivity, "Game Over", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isGameFinished(turn: Int): Boolean {
        return turn == 0
    }

}




