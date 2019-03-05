package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Memory.MemoryActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MemoryGameManager private constructor(private val context: Context){

    companion object : SingletonHolder<MemoryGameManager, Context>(::MemoryGameManager)

    //TODO : variable renaming
    private val animationTime = 350L
    private var halfTurn = false
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = ArrayList()
    var displayNewTurn = MutableLiveData<Int>()
    var displayNewScore = MutableLiveData<Int>()
    var score = 0
    var turn = 8
    private val rewards : MutableList<String> = ArrayList()
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var listOfCardsLiveData = MutableLiveData<ListOfCards>()
    private var list: MutableList<Pair<Drawable, String>> = ArrayList()
    var activity: MemoryActivity? = null
    private var listOfImgView: List<ImageView> = ArrayList()
    private var viewListeners = MutableLiveData<MutableList<Tile>>()


    fun initCardList(amount: Int, lisOfImageView: List<ImageView>, imageViewsListeners: MutableLiveData<MutableList<Tile>>) {
        listOfImgView = lisOfImageView
        viewListeners = imageViewsListeners
        listOfCardsLiveData = rickAndMortyAPI.getCardList(amount)
        listOfCardsLiveData.observeOnce(Observer {listOfCards ->
            getPicturesFromList(listOfCards)
        })

    }

    private fun getPicturesFromList(listOfCards: ListOfCards) {

        //TODO: find a better way to do this ...
        //Create a Drawable for each url
        for (card in listOfCards.cards!!) {
            Thread(Runnable {
                val result = try {
                    Drawable.createFromStream(
                            URL(card.cardImage).content as InputStream, card.cardName)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                    null
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                activity?.runOnUiThread {
                    list.add(Pair(result!!, card.cardName!!))
                    if (list.size == 6) {
                        initGame(listOfImgView, viewListeners, list)
                        //destroying reference
                        activity = null
                        Log.d(TAG, "___done")
                        return@runOnUiThread
                    }
                }
            }).start()
        }
    }

    private fun initGame(lisOfImageView: List<ImageView>, imageViewsListeners: MutableLiveData<MutableList<Tile>>, listOfPictures: MutableList<Pair<Drawable, String>>) {

        listOfPictures.addAll(listOfPictures)
        var finalListOfPictures = listOfPictures.toList()

        var rand = Math.abs(Random().nextInt() % 10) * 2
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
                    refName = ref,
                    placeHolder = R.drawable.card_back,
                    tapped = false,
                    tileView = imageView)
            )
        }

        for (view in lisOfImageView) {
            view.visibility = VISIBLE
        }
        imageViewsListeners.postValue(listOfTiles)

    }

    private fun isGameFinished(turn: Int): Boolean {
        return turn == 0
    }

    fun setRealImage(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onChange: Boolean) {
        var drawable: Drawable
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = context.getDrawable(placeHolder)!!//placeHolder
        view.setImageDrawable(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = image
            view.setImageDrawable(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onChange) clickedElements.onTwoTilesTapped(newTile = tile)
        }, animationTime)
        tile.tapped = !tile.tapped
    }

    fun setPlaceHolder(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onChange: Boolean) {
        var drawable: Drawable
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = image
        view.setImageDrawable(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = context.getDrawable(placeHolder)!!//placeHolder
            view.setImageDrawable(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onChange) clickedElements.onTwoTilesTapped(newTile = tile)
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

    // MutableList<Tile> extension
    private fun MutableList<Tile>.onTwoTilesTapped(newTile: Tile) {

        turn = isNewTurn()
        displayNewTurn.postValue(turn)

        // add only if different newTile or list empty
        val handler = Handler()
        if (this.isEmpty()) {
            this.add(newTile)
        } else if (newTile != this.first()) {
            this.add(newTile)
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
                rewards.add(newTile.refName)
                this.clear()
            }
        }

        if (isGameFinished(turn)) {
            for (item in listOfTiles) {
                item.tileView.setOnClickListener { }
            }
            Toast.makeText(context, String.format(context.getString(R.string.memory_game_over), score, rewards.toFormattedString()) , Toast.LENGTH_LONG).show()
        }
    }

    // MutableList<Int> extension
    private fun MutableList<String>.toFormattedString(): String {
        val result = StringBuilder()
        result.append(context.getString(R.string.cards_won))
        for (item in this) {
            result.append(item)
            result.append("\n")
        }
        return result.toString()
    }
}