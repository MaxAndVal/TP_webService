package com.example.lpiem.rickandmortyapp.View.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Games.KaamelottActivity
import com.example.lpiem.rickandmortyapp.View.Games.MemoryActivity
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : androidx.fragment.app.Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_kaamelott.setOnClickListener {
            val kaamelottIntent = Intent(context, KaamelottActivity::class.java)
            context?.startActivity(kaamelottIntent)
        }

        btn_memory.setOnClickListener {
            val memoryIntent = Intent(context, MemoryActivity::class.java)
            context?.startActivity(memoryIntent)
        }
    }

}

