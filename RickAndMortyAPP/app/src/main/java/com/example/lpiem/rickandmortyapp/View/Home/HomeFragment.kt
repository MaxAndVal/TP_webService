package com.example.lpiem.rickandmortyapp.View.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.ViewModel.Home.HomeManager
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Games.KaamelottActivity
import com.example.lpiem.rickandmortyapp.View.Games.MemoryActivity
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : androidx.fragment.app.Fragment() {

    private var loginAppManager: LoginAppManager? = null
    private var user: User? = null
    private var homeManager: HomeManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginAppManager = LoginAppManager.getInstance(context!!)
        user = loginAppManager?.connectedUser
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

        btn_kaamelott.setOnClickListener {
            val kaamelottIntent = Intent(context, KaamelottActivity::class.java)
            context?.startActivity(kaamelottIntent)
        }

        btn_memory.setOnClickListener {
            val memoryIntent = Intent(context, MemoryActivity::class.java)
            context?.startActivity(memoryIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        loginAppManager = LoginAppManager.getInstance(context!!)
        homeManager = HomeManager.getInstance(context!!)
    }

    override fun onDestroyView() {
        homeManager?.cancelCall()
        super.onDestroyView()
    }
}

