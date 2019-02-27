package com.example.lpiem.rickandmortyapp.View.Memory

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_memory.*
import java.util.*

const val animationTime = 500L

class MemoryActivity : AppCompatActivity() {

    private val animationTime = 500L
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)

        val listOfPictures: MutableList<Pair<Int, Int>> = mutableListOf(
                Pair(R.drawable.test1, 1), Pair(R.drawable.test2, 2),
                Pair(R.drawable.test3, 3), Pair(R.drawable.test4, 4),
                Pair(R.drawable.test5, 5), Pair(R.drawable.test6, 6)
        )

        listOfPictures.addAll(listOfPictures)
        val finalListOfPictures = listOfPictures.shuffled()

        val lisOfImageView = listOf(iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10,
                iv_11, iv_12)



        for (i in 0 until lisOfImageView.size) {
            lisOfImageView[i].tag = finalListOfPictures[i]
            val image = listOfPictures[i].first
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

    fun setRealImage(view: ImageView, placeHolder: Int, handler: Handler, image: Int, tile: Tile, onChange: Boolean) {
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

    fun setPlaceHolder(view: ImageView, placeHolder: Int, handler: Handler, image: Int, tile: Tile, onChange: Boolean) {
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

    fun MutableList<Tile>.onChange(tile: Tile) {
        // add only if different tile or list empty
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
            }
            else if (firstTile.image == secondTile.image) {

                Log.d(TAG, "firstTile tapped : ${firstTile.tapped}")
                Log.d(TAG, "secondTile tapped : ${secondTile.tapped}")
                firstTile.tapped = false
                secondTile.tapped = false

                firstTile.tileView.setOnClickListener { }
                secondTile.tileView.setOnClickListener { }
                this.clear()

            }
        }

    }


}




