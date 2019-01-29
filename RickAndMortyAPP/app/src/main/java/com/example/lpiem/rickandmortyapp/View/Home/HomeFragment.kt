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
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : androidx.fragment.app.Fragment() {

    private var homeManager: HomeManager? = null
    private lateinit var loginAppManager: LoginAppManager
    private var user : User? = null
    private var score = 0
    private var turn = 0


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
            makeGameDisplayed(false)
            homeManager!!.getRandomQuote()
        } else {
            gameOver()
        }


    }

    private fun isThereWinner(button: Button, solution: String) {
        if (turn<=5 && loginAppManager.gameInProgress) {
            if (button.text == solution ) {
                Toast.makeText(context, "Bonne réponse !!!", Toast.LENGTH_SHORT).show()
                tv_actual_score.text = String.format(getString(R.string.actual_score), ++score)
                tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++turn)
                if (turn <= 4) homeManager!!.getRandomQuote()
                if (turn == 5) {
                    gameOver()
                }
            } else {
                Toast.makeText(context, "Mauvaise réponse ...", Toast.LENGTH_SHORT).show()
                tv_actual_turn.text = String.format(getString(R.string.actual_turn), ++turn)
                if (turn <= 4) homeManager!!.getRandomQuote()
                if (turn == 5) {
                    gameOver()
                }
            }
        } else {
            loginAppManager.gameInProgress = false
            val handler = Handler()
            handler.postDelayed({
                Log.d(TAG, "date : ${getDate()}")
                gameOver()
            }, 1000L)
        }
    }



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

    internal fun updateUI(listResult: Triple<String, String, List<String>>) {
        val solution = listResult.second
        if (solution != "") {
            tv_citation.text = listResult.first
            val shuffleList = listOf(solution, listResult.third[0], listResult.third[1], listResult.third[2]).shuffled()
            val buttons = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4)
            var i = 0
            buttons.forEach {
                it.text = shuffleList[i++]
                it.setOnClickListener { view -> isThereWinner(view as Button, solution) }
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

    private fun gameOver() {
        val handler = Handler()
        loginAppManager.gameInProgress = false
        handler.postDelayed({
            Log.d(TAG, "date : ${getDate()}")
            val UIElements = listOf(btn_perso1, btn_perso2, btn_perso3, btn_perso4, tv_citation, tv_actual_turn, tv_actual_score)
            for (element in UIElements) {
                element.visibility = GONE
            }
            tv_game_over.visibility = VISIBLE
            tv_final_score.let {
                it.visibility = VISIBLE
                it.text = String.format(getString(R.string.final_score), score)
            }
        }, 1000L)
    }

    private fun getDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        val time = cal.time
        val formattedDate = SimpleDateFormat("dd-mm-yyyy", Locale.FRENCH)
        return formattedDate.format(time)
    }

}

