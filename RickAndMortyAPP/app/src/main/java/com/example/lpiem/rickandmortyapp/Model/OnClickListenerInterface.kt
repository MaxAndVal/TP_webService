package com.example.lpiem.rickandmortyapp.Model

interface OnClickListenerInterface {

    fun addFriends(item:Friend)

    fun delFriends(item:Friend): Boolean
}