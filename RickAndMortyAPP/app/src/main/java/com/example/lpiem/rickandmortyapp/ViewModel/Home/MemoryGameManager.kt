package com.example.lpiem.rickandmortyapp.ViewModel.Home

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.Games.MemoryReward
import com.example.lpiem.rickandmortyapp.Model.Games.Tile
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Card
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.UserResponse
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MemoryGameManager constructor( val context: Context) {

    private var loginAppManager = LoginAppManager.getInstance(context)
    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val animationTime = 350L
    var score = 0
    var turn = 8
    private var halfTurn = false
    // LISTS
    var list: MutableList<Triple<Drawable, String, Int>> = ArrayList()
    private var listOfTiles: MutableList<Tile> = ArrayList()
    private var clickedElements: MutableList<Tile> = ArrayList()
    private val rewards: MutableList<MemoryReward> = ArrayList()
    // LIVE DATA
    var startTheGame = MutableLiveData<Unit>()
    var displayNewTurn = MutableLiveData<Int>()
    var displayNewScore = MutableLiveData<Int>()
    var rewardsReceiver = MutableLiveData<MutableList<MemoryReward>>()
    var drawableListReceiver = MutableLiveData<Pair<MutableList<Drawable?>, ListOfCards>>()
    var finalViewListeners = MutableLiveData<MutableList<Tile>>()
    private var rewardToApiResult = MutableLiveData<UserResponse>()
    private var userResponseReceiver = MutableLiveData<UserResponse>()
    private var listOfCardsReceiver = MutableLiveData<ListOfCards>()
    private lateinit var UserResponseObserver: Observer<UserResponse>


    fun cancelCall() {
        rickAndMortyAPI.cancelCall()
    }

    fun initCardList(amount: Int) {
        listOfCardsReceiver = rickAndMortyAPI.getCardList(amount)
        listOfCardsReceiver.observeOnce(Observer { listOfCards ->
            getPicturesFromList(listOfCards)
        })
    }

    private fun getPicturesFromList(listOfCards: ListOfCards) {
        Thread {
            val resultList: MutableList<Drawable?> = ArrayList()

            for (card in listOfCards.cards ?: listOf(Card(null))) {

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
                resultList.add(result)
            }

            drawableListReceiver.postValue(Pair(resultList, listOfCards))
        }.start()
    }

    fun initGame(lisOfImageView: List<ImageView>,
                 listOfPictures: MutableList<Triple<Drawable, String, Int>>) {

        listOfPictures.addAll(listOfPictures)
        var finalListOfPictures = listOfPictures.toList()

        // Real random shuffle
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
                    tileView = imageView,
                    cardId = finalListOfPictures[position].third)
            )
        }

        for (view in lisOfImageView) {
            view.visibility = VISIBLE
        }
        finalViewListeners.postValue(listOfTiles)
    }

    private fun isGameFinished(turn: Int): Boolean {
        if (rewards.size == 6) {
            this.turn = 0
            return true
        }
        return turn == 0
    }

    fun setRealImage(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onTwoTilesTapped: Boolean) {
        view.animate().scaleX(0f).setDuration(animationTime).start()
        view.setImageDrawable(context.getDrawable(placeHolder)!!)
        view.isClickable = false
        handler.postDelayed({
            view.setImageDrawable(image)
            view.animate().scaleX(1f).setDuration(animationTime).start()
            view.isClickable = true
            if (onTwoTilesTapped) clickedElements.onTwoTilesTapped(newTile = tile)
        }, animationTime)
        tile.tapped = !tile.tapped
    }

    fun setPlaceHolder(view: ImageView, placeHolder: Int, handler: Handler, image: Drawable, tile: Tile, onTwoTilesTapped: Boolean) {
        view.animate().scaleX(0f).setDuration(animationTime).start()
        view.setImageDrawable(image)
        view.isClickable = false
        handler.postDelayed({
            view.setImageDrawable(context.getDrawable(placeHolder)!!)
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
            if (firstTile.cardId != secondTile.cardId) {
                handler.postDelayed({
                    setPlaceHolder(firstTile.tileView, firstTile.placeHolder, handler, firstTile.image, firstTile, false)
                    setPlaceHolder(secondTile.tileView, secondTile.placeHolder, handler, secondTile.image, secondTile, false)
                    this.clear()
                }, animationTime)
            } else if (firstTile.cardId == secondTile.cardId) {
                score++
                displayNewScore.postValue(score)
                val toast = Toast.makeText(context, context.getString(R.string.youAlreadyWonThisTile), Toast.LENGTH_SHORT)

                firstTile.tileView.setOnClickListener {
                    toast.show()
                }
                secondTile.tileView.setOnClickListener {
                    toast.show()
                }
                rewards.add(MemoryReward(newTile.cardId, newTile.refName))
                this.clear()
            }
        }

        if (isGameFinished(turn)) {
            for (item in listOfTiles) {
                item.tileView.setOnClickListener { }
            }
            list.clear()
            UserResponseObserver = Observer {
                if (it.code == SUCCESS) {
                    Log.d(TAG, "code: ${it.code} message: ${it.message}")
                } else {
                    Log.d(TAG, "error code: ${it.code} message: ${it.message}")
                }
            }
            rewardToApiResult = rickAndMortyAPI.addRewardsToUser(rewards, loginAppManager.connectedUser!!.userId!!)
            rewardToApiResult.observeOnce(UserResponseObserver)
            rewardsReceiver.postValue(rewards)
            putDateToken()
        }
    }

    // Date and game available
    fun gameAvailable(user: User) {
        userResponseReceiver = rickAndMortyAPI.getUserById(user.userId)
        userResponseReceiver.observeOnce(Observer {
            getUserByIdTreatment(it)
        })
    }

    private fun getUserByIdTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            val user = userResponse.user
            loginAppManager.memoryInProgress = getDate() != user?.userLastMemory
            Log.d(TAG, "user Date : ${user?.userLastMemory} <==> Date : ${getDate()}")
            startTheGame.postValue(Unit)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message_userNotFound), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun putDateToken() {
        val id = loginAppManager.connectedUser!!.userId
        userResponseReceiver = rickAndMortyAPI.putMemoryDateToken(getDate(), id)
        userResponseReceiver.observeOnce(Observer {
            putDateTreatment(it)
        })
    }

    private fun putDateTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            Log.d(TAG, "success code : $code, message $message")
        } else {
            Log.d(TAG, "error code : $code, message $message")
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        return formatter.format(date)
    }

}