package com.example.lpiem.rickandmortyapp.View.Home

interface HomeDisplayUI {

    fun updateUI(citation: String, solution: String, list: List<String>)

    fun displayActivityContent()

    //fun updatePickleRicksAmount(arg1: Int, arg2: String)
}