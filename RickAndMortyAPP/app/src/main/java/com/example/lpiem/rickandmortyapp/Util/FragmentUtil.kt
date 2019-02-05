package com.example.lpiem.rickandmortyapp.Util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentUtil {

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment, frameId: Int) {

        val transaction = manager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()

    }
}