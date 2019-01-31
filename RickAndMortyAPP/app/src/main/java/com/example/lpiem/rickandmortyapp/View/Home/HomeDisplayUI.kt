package com.example.lpiem.rickandmortyapp.View.Home

interface HomeDisplayUI {

    fun updateUI(listResult: Triple<String, String, List<String>>)

    fun displayFragmentContent()

    fun updatePickleRicksAmount(arg1: Int, arg2: String)
}