package com.example.lpiem.rickandmortyapp.Model

interface SocialActionsInterface {

    fun addFriends(item:Friend)

    fun delFriends(item:Friend): Boolean
}