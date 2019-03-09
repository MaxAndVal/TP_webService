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
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.Tile
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.TAG
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MemoryGameManager private constructor(private val context: Context) {

    companion object : SingletonHolder<MemoryGameManager, Context>(::MemoryGameManager)

    //TODO : variable renaming
    private var loginAppManager = LoginAppManager.getInstance(context)
    private var responseFromApiLiveData = MutableLiveData<ResponseFromApi>()
    private lateinit var startTheGame: MutableLiveData<Unit>
    private val animationTime = 350L
    private var halfTurn = false
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = ArrayList()
    var displayNewTurn = MutableLiveData<Int>()
    var displayNewScore = MutableLiveData<Int>()
    var score = 0
    var turn = 8
    private val rewards: MutableList<String> = ArrayList()
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var listOfCardsLiveData = MutableLiveData<ListOfCards>()
    var list: MutableList<Pair<Drawable, String>> = ArrayList()
    private lateinit var listOfImgView: List<ImageView>
    private lateinit var viewListeners: MutableLiveData<MutableList<Tile>>
    private lateinit var rewardsLiveData: MutableLiveData<MutableList<String>>
    @Volatile
    private lateinit var thread: Thread
    private lateinit var drawableListLiveData: MutableLiveData<Pair<MutableList<Drawable?>, ListOfCards>>


    fun initCardList(amount: Int, lisOfImageView: List<ImageView>, imageViewsListeners: MutableLiveData<MutableList<Tile>>, rewardsListener: MutableLiveData<MutableList<String>>, drawLiveData: MutableLiveData<Pair<MutableList<Drawable?>, ListOfCards>>) {
        drawableListLiveData = drawLiveData
        rewardsLiveData = rewardsListener
        listOfImgView = lisOfImageView
        viewListeners = imageViewsListeners
        listOfCardsLiveData = rickAndMortyAPI.getCardList(amount)
        listOfCardsLiveData.observeOnce(Observer { listOfCards ->
            getPicturesFromList(listOfCards)
        })

    }

    fun interruptThread() {
        thread.interrupt()
    }

    private fun getPicturesFromList(listOfCards: ListOfCards) {

        val runnable = Runnable {
            val resultList: kotlin.collections.MutableList<android.graphics.drawable.Drawable?> = ArrayList()
            for (card in listOfCards.cards!!) {

                val result = try {
                    android.graphics.drawable.Drawable.createFromStream(
                            java.net.URL(card.cardImage).content as java.io.InputStream, card.cardName)
                } catch (e: java.net.MalformedURLException) {
                    e.printStackTrace()
                    null
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                    null
                }
                resultList.add(result)
            }
            Log.d(TAG, resultList.toString())
            drawableListLiveData.postValue(kotlin.Pair(resultList, listOfCards))
        }

        thread = Thread(runnable)
        thread.start()

    }

    fun initGame(lisOfImageView: List<ImageView>,
                 imageViewsListeners: MutableLiveData<MutableList<Tile>>,
                 listOfPictures: MutableList<Pair<Drawable, String>>) {

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
                    placeHolder = R.drawable.memory_card_back,
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

    fun setRealImage(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onTwoTilesTapped: Boolean) {
        var drawable: Drawable
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = context.getDrawable(placeHolder)!!
        view.setImageDrawable(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = image
            view.setImageDrawable(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onTwoTilesTapped) clickedElements.onTwoTilesTapped(newTile = tile)
        }, animationTime)
        tile.tapped = !tile.tapped
    }

    fun setPlaceHolder(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onTwoTilesTapped: Boolean) {
        var drawable: Drawable
        view.animate().scaleX(0f).setDuration(animationTime).start()
        drawable = image
        view.setImageDrawable(drawable)
        view.isClickable = false
        handler.postDelayed({
            drawable = context.getDrawable(placeHolder)!!
            view.setImageDrawable(drawable)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onTwoTilesTapped) clickedElements.onTwoTilesTapped(newTile = tile)
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
            list.clear()
            rewardsLiveData.postValue(rewards)
            loginAppManager.memoryInProgress = false
            putDateToken()
        }
    }

    // Date and game available

    fun gameAvailable(user: User, link: MutableLiveData<Unit>) {
        startTheGame = link
        responseFromApiLiveData = rickAndMortyAPI.getUserById(user.userId)
        responseFromApiLiveData.observeOnce(Observer {
            getUserByIdTreatment(it)
        })
    }

    private fun getUserByIdTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val user = response.results
            loginAppManager.memoryInProgress = getDate() != user?.userLastMemory
            Log.d(TAG, "user Date : ${user?.userLastMemory} , date : ${getDate()}")
            startTheGame.postValue(Unit)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message_userNotFound), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun putDateToken() {
        val id = loginAppManager.connectedUser!!.userId
        responseFromApiLiveData = rickAndMortyAPI.putMemoryDateToken(getDate(),id)
        responseFromApiLiveData.observeOnce(Observer {
            putDateTreatment(it)
        })
    }

    private fun putDateTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        Log.d(TAG, "date = $date")
        Log.d(TAG,"date formatted : ${formatter.format(date)}")
        return formatter.format(date)
    }

}