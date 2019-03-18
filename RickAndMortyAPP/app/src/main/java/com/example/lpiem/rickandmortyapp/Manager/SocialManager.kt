package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel.LIST_OF_FRIENDS
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel.LIST_OF_FRIENDS_REQUESTS
import com.example.lpiem.rickandmortyapp.Model.UserResponse
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.TAG


class SocialManager private constructor(private val context: Context){

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private val loginAppManager = LoginAppManager.getInstance(context)

    var listOfFriends: ListOfFriends? = null
    var listOfActualFriends: List<Friend>? = ArrayList()
    var listOfPotentialFriends: List<Friend>? = ArrayList()
    var resultFromSearch: ListOfFriends? = null

    private var listOfFriendsLiveData = MutableLiveData<ListOfFriends>()
    private var searchForFriendsLiveData = MutableLiveData<ListOfFriends>()
    private var callForFriendLiveData = MutableLiveData<UserResponse>()
    private var validateFriendLiveData = MutableLiveData<UserResponse>()
    private var delFriendLiveData = MutableLiveData<UserResponse>()
    var updateListLiveData = MutableLiveData<List<Friend>?>()
    var changeBtnActionLiveData = MutableLiveData<SocialListLabel>()
    var loaderLiveData = MutableLiveData<Boolean>()

    companion object : SingletonHolder<SocialManager, Context>(::SocialManager)

    @Synchronized
    fun getListOfFriends(userId: Int) {
        listOfFriendsLiveData = rickAndMortyAPI.getFriendsList(userId)
        listOfFriendsLiveData.observeOnce(Observer {
            listOfFriendsTreatment(it)
        })
    }

    private fun addFriendTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        when (code) {
            SUCCESS -> {
                val result = userResponse.user
                Log.d(TAG, "code = $code message = $message result = $result")
                getListOfFriends(loginAppManager.connectedUser!!.userId!!)
                Toast.makeText(context, context.getString(R.string.friend_demand_added), Toast.LENGTH_SHORT).show()
            }
            null -> Toast.makeText(context, "Vous etes déjà amis !", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun delFriendTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            getListOfFriends(loginAppManager.connectedUser!!.userId!!)
            Toast.makeText(context, "Ami supprimé de votre liste", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun acceptFriendshipTreatment(userResponse: UserResponse) {
        val code = userResponse.code
        val message = userResponse.message
        if (code == SUCCESS) {
            Toast.makeText(context, "Vous etes maintenant amis !", Toast.LENGTH_SHORT).show()
            loaderLiveData.postValue(false)
        }
        else Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
    }

    private fun resultFriendsSearchingTreatment(list: ListOfFriends) {
        val code = list.code
        val message = list.message
        Log.d(TAG, "list of friends searching : $list")
        if (code == SUCCESS) {
            resultFromSearch = list
            updateListLiveData.postValue(list.friends)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun listOfFriendsTreatment(list: ListOfFriends) {
        val code = list.code
        val message = list.message
        if (code == SUCCESS) {
            listOfFriends = list
            listOfActualFriends = list.friends?.filter { it.accepted == true }
            listOfPotentialFriends = list.friends?.filter { it.accepted == false }
            updateListLiveData.postValue(listOfActualFriends)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
        changeBtnActionLiveData.postValue(LIST_OF_FRIENDS)
    }

    fun searchForFriends(friends: String?) {
        val currentUserId = loginAppManager.connectedUser!!.userId!!
        searchForFriendsLiveData = rickAndMortyAPI.getFriendSearchResult(currentUserId, friends)
        searchForFriendsLiveData.observeOnce(Observer {
            resultFriendsSearchingTreatment(it)
        })
    }

    fun friendsRequest() {
        updateListLiveData.postValue(listOfPotentialFriends)
        changeBtnActionLiveData.postValue(LIST_OF_FRIENDS_REQUESTS)
    }

    fun callForAddFriend(friend: Friend) {
        val currentUserId = loginAppManager.connectedUser!!.userId!!
        callForFriendLiveData = rickAndMortyAPI.addThisFriend(currentUserId, friend.userId!!)
        callForFriendLiveData.observeOnce(Observer {
            addFriendTreatment(it)
        })
    }

    fun callForValidateFriend(friend: Friend) {
        val currentUserId = loginAppManager.connectedUser!!.userId!!
        validateFriendLiveData = rickAndMortyAPI.validateFriendship(currentUserId, friend.userId!!)
        validateFriendLiveData.observeOnce(Observer {
            acceptFriendshipTreatment(it)
        })
    }

    fun callToDelFriend(friend: Friend) {
        val currentUserId = loginAppManager.connectedUser!!.userId!!
        delFriendLiveData = rickAndMortyAPI.deleteThisFriend(currentUserId, friend.userId!!)
        delFriendLiveData.observeOnce(Observer {
            delFriendTreatment(it)
        })
    }
}