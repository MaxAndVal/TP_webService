package com.example.lpiem.rickandmortyapp.View.Home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.HomeManager
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_home.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : androidx.fragment.app.Fragment() {

    private var homeManager: HomeManager? = null
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
    private var score = 0
    private var turn = 0

    private var param1: String? = null
    private var param2: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CollectionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        loginAppManager = LoginAppManager.getInstance(context!!)
        user = loginAppManager.connectedUser
        Log.d(TAG, "user : $user")

        homeManager = HomeManager.getInstance(context!!)
        homeManager?.getFragment(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (loginAppManager.gameInProgress) {
            homeManager?.gameAvailable(loginAppManager.connectedUser)
        } else {
            gameOver()
        }
    }

    fun displayFragmentContent() {
        if (loginAppManager.gameInProgress) {
            makeGameDisplayed(false)
            homeManager!!.getRandomQuote()
        } else {
            gameOver()
        }
    }

    internal fun updateUI(listResult: Triple<String, String, List<String>>) {
        val solution = listResult.second
        if (solution != "") {
            tv_citation.text = listResult.first
            val shuffleList = listOf(solution, listResult.third[0], listResult.third[1], listResult.third[2]).shuffled()
            val buttons = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4)
            var i = 0
            buttons.forEach {
                it.text = shuffleList[i++]
                it.setOnClickListener { view -> checkForWinner(view as Button, solution) }
            }
            makeGameDisplayed(true)
        } else {
            Log.d(TAG, "listResult from API : $listResult")
        }
    }

    private fun makeGameDisplayed(display: Boolean) {
        val displayedItems = listOf(tv_citation, btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_actual_turn, tv_actual_score)
        if (display) {
            tv_actual_turn.text = String.format(getString(R.string.actual_turn, turn))
            tv_actual_score.text = String.format(getString(R.string.actual_score, score))
            for (item in displayedItems) {
                item.visibility = VISIBLE
            }
        } else {
            for (item in displayedItems) {
                item.visibility = GONE
            }
        }
    }

    private fun checkForWinner(button: Button, solution: String) {
        if (turn <= 5 && loginAppManager.gameInProgress) {
            if (button.text == solution) {
                Toast.makeText(context, getString(R.string.good_answer), Toast.LENGTH_SHORT).show()
                tv_actual_score.text = String.format(getString(R.string.actual_score), ++score)
                tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++turn)
                if (turn <= 4) homeManager!!.getRandomQuote()
                if (turn == 5) {
                    gameOver()
                    homeManager?.putDateToken()
                }
            } else {
                Toast.makeText(context, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show()
                tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++turn)
                if (turn <= 4) homeManager!!.getRandomQuote()
                if (turn == 5) {
                    gameOver()
                    homeManager?.putDateToken()
                }
            }
        } else {

            val handler = Handler()
            handler.postDelayed({
                gameOver()
            }, loginAppManager.handlerTime)
        }
    }

    private fun gameOver() {
        val handler = Handler()
        handler.postDelayed({
            val UIElements = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_citation, tv_actual_turn, tv_actual_score)
            for (element in UIElements) {
                element.visibility = GONE
            }
            tv_game_over.visibility = VISIBLE
            if (loginAppManager.gameInProgress) {
                Toast.makeText(context, "Partie terminée. Votre score final est de $score.\nVous avez gagné ${score * 10} PickleRick !!", Toast.LENGTH_LONG).show()
                homeManager?.updatePickleRick(score)
            }
            loginAppManager.gameInProgress = false
            loginAppManager.handlerTime = 0L
        }, loginAppManager.handlerTime)
    }

}

