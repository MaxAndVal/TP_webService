package com.example.lpiem.rickandmortyapp.View.Home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Manager.HomeManager
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.fragment_home.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : androidx.fragment.app.Fragment(), HomeDisplayUI {

    private var homeManager: HomeManager? = null
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginAppManager = LoginAppManager.getInstance(context!!)
        homeManager = HomeManager.getInstance(context!!)
        if (loginAppManager.gameInProgress) {
            homeManager?.gameAvailable(loginAppManager.connectedUser!!, this)
        } else {
            gameOver(false)
        }
    }

    override fun displayFragmentContent() {
        if (loginAppManager.gameInProgress) {
            makeGameDisplayed(false)
            homeManager!!.getRandomQuote(this)
        } else {
            gameOver(false)
        }
    }

    override fun updateUI(citation: String, solution: String, list: List<String>) {
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
            Log.d(TAG, "listResult from API : $list")
        }
    }

    override fun updatePickleRicksAmount(arg1: Int, arg2: String) {
        (activity as BottomActivity).tv_wallet.text = String.format(context!!.getString(R.string.wallet_amount), arg1, arg2)
    }

    private fun makeGameDisplayed(display: Boolean) {
        val displayedItems = listOf(tv_citation, btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_actual_turn, tv_actual_score)
        if (display) {
            tv_actual_turn.text = String.format(getString(R.string.actual_turn, homeManager!!.turn))
            tv_actual_score.text = String.format(getString(R.string.actual_score, homeManager!!.score))
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
        if (homeManager!!.turn <= 5 && loginAppManager.gameInProgress) {
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
                    view.visibility = GONE
                }
                tv_game_over.visibility = VISIBLE
                if (loginAppManager.gameInProgress) {
                    val toast = Toast.makeText(context, String.format(getString(R.string.game_is_over), homeManager!!.score, homeManager!!.score * 10), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 250)
                    toast.show()
                    homeManager?.updatePickleRick(homeManager!!.score)
                }
                loginAppManager.gameInProgress = false
            }
            resetScore()
        }, handlerTime)

    }

    private fun resetScore() {
        homeManager?.score = 0
        homeManager?.turn = 0
    }

    private fun displayForAnswer(goodAnswer: Boolean) {
        if (goodAnswer) {
            Toast.makeText(context, getString(R.string.good_answer), Toast.LENGTH_SHORT).show()
            tv_actual_score.text = String.format(getString(R.string.actual_score), ++homeManager!!.score)
            tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++homeManager!!.turn)
        } else {
            Toast.makeText(context, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show()
            tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++homeManager!!.turn)
        }
    }

    private fun checkForTurn() {
        if (homeManager!!.turn <= 4) homeManager!!.getRandomQuote(this)
        if (homeManager!!.turn == 5) {
            gameOver(true)
            homeManager?.putDateToken()
        }
    }

    override fun onDestroyView() {
        homeManager?.cancelCall()
        super.onDestroyView()
    }
}

