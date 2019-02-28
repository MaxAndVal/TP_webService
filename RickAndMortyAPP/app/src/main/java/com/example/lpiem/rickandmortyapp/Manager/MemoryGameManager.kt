package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import java.util.*

class MemoryGameManager private constructor(private val context: Context){

    companion object : SingletonHolder<MemoryGameManager, Context>(::MemoryGameManager)

    private val animationTime = 500L
    private var halfTurn = false
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = ArrayList()
    var displayNewTurn = MutableLiveData<Int>()
    var displayNewScore = MutableLiveData<Int>()
    var score = 0
    var turn = 8

    //TODO: manage web call to grab list of unique images
    fun initList(lisOfImageView: List<ImageView>, imageViewsListeners: MutableLiveData<MutableList<Tile>>) {

        val listOfPictures: MutableList<Pair<Int, Int>> = mutableListOf(
                Pair(R.drawable.test1, 1), Pair(R.drawable.test2, 2),
                Pair(R.drawable.test3, 3), Pair(R.drawable.test4, 4),
                Pair(R.drawable.test5, 5), Pair(R.drawable.test6, 6)
        )

        var rand = Math.abs(Random().nextInt() % 10) * 2
        listOfPictures.addAll(listOfPictures)
        var finalListOfPictures = listOfPictures.toList()

        while (rand > 0) {
            finalListOfPictures = finalListOfPictures.shuffled()
            rand--
        }

        for (position in 0 until lisOfImageView.size) {
            lisOfImageView[position].tag = finalListOfPictures[position]
            val image = finalListOfPictures[position].first
            val ref = finalListOfPictures[position].second
            val imageView = lisOfImageView[position]
            listOfTiles.add(Tile(image = image,
                    refId = ref,
                    placeHolder = R.drawable.card_back,
                    tapped = false,
                    tileView = imageView)
            )
        }

        imageViewsListeners.postValue(listOfTiles)
    }

    private fun isGameFinished(turn: Int): Boolean {
        return turn == 0
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

    private fun isNewTurn(): Int {
        halfTurn = if (halfTurn) {
            turn--
            !halfTurn
        } else {
            true
        }
        return turn
    }

    // extension
    private fun MutableList<Tile>.onChange(tile: Tile) {

        turn = isNewTurn()
        displayNewTurn.postValue(turn)

        // add only if different tile or list empty
        val handler = Handler()
        if (this.isEmpty()) {
            this.add(tile)
        } else if (tile != this.first()) {
            this.add(tile)
        }

        if (this.size % 2 == 0 && this.size != 0) {
            val firstTile = this.first()
            val secondTile = this.last()
            if (firstTile.image != secondTile.image) {
                handler.postDelayed({
                    setPlaceHolder(firstTile.tileView, firstTile.placeHolder, handler, firstTile.image, firstTile, false)
                    setPlaceHolder(secondTile.tileView, secondTile.placeHolder, handler, secondTile.image, secondTile, false)
                    this.clear()
                }, animationTime)
            } else if (firstTile.image == secondTile.image) {
                score++
                displayNewScore.postValue(score)
                val toast = Toast.makeText(context, context.getString(R.string.youAlreadyWonThisTile), Toast.LENGTH_SHORT)

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
                item.tileView.setOnClickListener { }
            }
            Toast.makeText(context, String.format(context.getString(R.string.memory_game_over), score, " "), Toast.LENGTH_SHORT).show()
        }
    }

}