package com.example.lpiem.rickandmortyapp.View.Kaamelott

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.KaamelottManager
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.KaamelottQuizBundle
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_kaamelott.*

class KaamelottActivity : AppCompatActivity() {

    private var kaamelottManager = KaamelottManager.getInstance(this)
    private var loginAppManager = LoginAppManager.getInstance(this)
    private var user: User? = null
    private lateinit var initDisplayContentObserver: Observer<Unit>
    private lateinit var updateUIObserver: Observer<KaamelottQuizBundle>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kaamelott)

        user = loginAppManager.connectedUser
        Log.d(com.example.lpiem.rickandmortyapp.View.TAG, "user : $user")

        initDisplayContentObserver = Observer {
            if (loginAppManager.gameInProgress) {
                makeGameDisplayed(false)
                kaamelottManager.getRandomQuote()
            } else {
                gameOver(false)
            }
        }

        updateUIObserver = Observer { bundle ->
            val citation = bundle.citation
            val solution = bundle.solution
            val list = bundle.list
            if (solution != "" && loginAppManager.connectedUser != null) {
                tv_citation.text = citation
                val buttons = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4)
                var i = 0
                buttons.forEach {
                    it.text = list[i++]
                    it.setOnClickListener { view -> checkForWinner(view as Button, solution) }
                }
                makeGameDisplayed(true)
            } else {
                Log.d(com.example.lpiem.rickandmortyapp.View.TAG, "listResult from API : $list")
            }
        }

        kaamelottManager.updateUI.observeForever(updateUIObserver)
        kaamelottManager.initDisplayContent.observeForever(initDisplayContentObserver)
        if (loginAppManager.gameInProgress) {
            kaamelottManager.gameAvailable(loginAppManager.connectedUser!!)
        } else {
            gameOver(false)
        }

    }

    private fun makeGameDisplayed(display: Boolean) {
        val displayedItems = listOf(tv_citation, btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_actual_turn, tv_actual_score)
        if (display) {
            tv_actual_turn.text = String.format(getString(R.string.actual_turn, kaamelottManager.turn))
            tv_actual_score.text = String.format(getString(R.string.actual_score, kaamelottManager.score))
            for (item in displayedItems) {
                item.visibility = View.VISIBLE
            }
        } else {
            for (item in displayedItems) {
                item.visibility = View.GONE
            }
        }
    }

    private fun checkForWinner(button: Button, solution: String) {
        if (kaamelottManager.turn <= 5 && loginAppManager.gameInProgress) {
            if (button.text == solution) {
                displayForAnswer(true)
                checkForTurn()
            } else {
                displayForAnswer(false)
                checkForTurn()
            }
        } else {
            gameOver(true)
        }
    }

    private fun gameOver(withHandler: Boolean) {
        var handlerTime = 0L
        if (withHandler) handlerTime = 1000L
        val handler = Handler()
        handler.postDelayed({
            if (loginAppManager.connectedUser != null) {
                val views = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_citation, tv_actual_turn, tv_actual_score)
                for (view in views) {
                    view.visibility = View.GONE
                }
                tv_game_over.visibility = View.VISIBLE
                if (loginAppManager.gameInProgress) {
                    val toast = Toast.makeText(this, String.format(getString(R.string.game_is_over), kaamelottManager.score, kaamelottManager.score * 10), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 250)
                    toast.show()
                    kaamelottManager.updatePickleRick(kaamelottManager.score)
                }
                loginAppManager.gameInProgress = false
            }
            resetScore()
        }, handlerTime)

    }

    private fun resetScore() {
        kaamelottManager.score = 0
        kaamelottManager.turn = 0
    }

    private fun displayForAnswer(goodAnswer: Boolean) {
        if (goodAnswer) {
            Toast.makeText(this, getString(R.string.good_answer), Toast.LENGTH_SHORT).show()
            tv_actual_score.text = String.format(getString(R.string.actual_score), ++kaamelottManager.score)
            tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++kaamelottManager.turn)
        } else {
            Toast.makeText(this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show()
            tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++kaamelottManager.turn)
        }
    }

    private fun checkForTurn() {
        if (kaamelottManager.turn <= 4) kaamelottManager.getRandomQuote()
        if (kaamelottManager.turn == 5) {
            gameOver(true)
            kaamelottManager.putDateToken()
        }
    }

    override fun onBackPressed() {
        kaamelottManager.cancelCall()
        kaamelottManager.updateUI.removeObserver(updateUIObserver)
        kaamelottManager.initDisplayContent.removeObserver(initDisplayContentObserver)
        super.onBackPressed()
    }
}
