package com.example.lpiem.rickandmortyapp.View.Social

import com.example.lpiem.rickandmortyapp.Model.Friend

interface SocialActionsInterface {

    fun addFriends(item: Friend)

    fun delFriends(item: Friend): Boolean
}