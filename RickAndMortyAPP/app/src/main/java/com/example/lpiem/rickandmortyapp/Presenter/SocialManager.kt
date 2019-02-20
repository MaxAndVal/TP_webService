package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Social.SocialActionsInterface
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_social.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SocialManager private constructor(private val context: Context){

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context).instance
    var socialFragment: SocialFragment? = null
    private val loginAppManager = LoginAppManager.getInstance(context)
    private lateinit var link : SocialActionsInterface

    companion object : SingletonHolder<SocialManager, Context>(::SocialManager)

    fun captureFragmentInstance(fragment: SocialFragment) {
        socialFragment = fragment
    }

    @Synchronized
    fun getListOfFriends(userId: Int, link: SocialActionsInterface) {
        val resultCall = rickAndMortyAPI!!.getListOfFriends(userId)
        callRetrofit(resultCall, RetrofitCallTypes.LIST_OF_FRIENDS)
        this.link = link
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.LIST_OF_FRIENDS -> {
                            listOfFriendsTreatment(result as ListOfFriends)
                        }
                        RetrofitCallTypes.RESULT_FRIENDS_SEARCHING -> {
                            resultFriendsSearchingTreatment(result as ListOfFriends)
                        }
                        RetrofitCallTypes.ACCEPT_FRIENDSHIP ->{
                            acceptFriendshipTreatment(result as ResponseFromApi)
                        }
                        RetrofitCallTypes.DEL_A_FRIEND -> {
                            delFriendTreatment(result as ResponseFromApi)
                        }
                        RetrofitCallTypes.ADD_A_FRIENDS -> {
                            addFriendTreatment(result as ResponseFromApi)
                        }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    private fun addFriendTreatment(response: ResponseFromApi) {
        //TODO: need implementation for rv
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val result = response.results
            Toast.makeText(context, "code $code added $message results : $result", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun delFriendTreatment(response: ResponseFromApi) {
        //TODO: need implementation for rv
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val results = response.results
            Toast.makeText(context, "code : $code - delete : $message results : $results", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun acceptFriendshipTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val results = response.results
            Toast.makeText(context, "code : $code, message $message , results $results", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "code error : $code, message $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resultFriendsSearchingTreatment(list: ListOfFriends) {
        val code = list.code
        val message = list.message
        Log.d(TAG, "list of friends searching : $list")
        if (code == SUCCESS) {
            socialFragment?.resultFromSearch = list
            socialFragment?.updateDataSetList(list.friends)
        } else {
            Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listOfFriendsTreatment(list: ListOfFriends) {
        val code = list.code
        val message = list.message
        if (code == SUCCESS) {
            socialFragment?.listOfFriends = list
            socialFragment?.listOfActualFriends = list.friends?.filter { it.accepted == true }
            socialFragment?.listOfPotentialFriends = list.friends?.filter { it.accepted == false }
            Log.d(TAG, "List : " + socialFragment?.listOfFriends)
            Log.d(TAG, "List actual : " + socialFragment?.listOfActualFriends)
            Log.d(TAG, "List potential : " + socialFragment?.listOfPotentialFriends)

            socialFragment?.updateDataSetList(socialFragment?.listOfActualFriends)
        } else {
            Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
        }
        if (socialFragment != null && socialFragment!!.isVisible) {
            socialFragment?.btn_friendsRequest?.text = context.getString(R.string.btn_pending_friend_requests)
            socialFragment?.btn_friendsRequest?.setOnClickListener { friendsRequest(link) }
        }
    }

    fun searchForFriends(friends: String?) {
        val resultCall = rickAndMortyAPI!!.searchForFriends(loginAppManager.connectedUser!!.userId!!, friends)
        callRetrofit(resultCall, RetrofitCallTypes.RESULT_FRIENDS_SEARCHING)
    }

    fun friendsRequest(link: SocialActionsInterface) {
        socialFragment?.updateDataSetList(socialFragment?.listOfPotentialFriends)
        socialFragment?.btn_friendsRequest?.text = context.getString(R.string.btn_list_of_friends)
        socialFragment?.btn_friendsRequest?.setOnClickListener { getListOfFriends(loginAppManager.connectedUser!!.userId!!, link) }
    }

    fun callForAddFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.addAFriend( loginAppManager.connectedUser!!.userId!!, item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.ADD_A_FRIENDS)
    }

    fun callForValidateFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.validateAFriend( loginAppManager.connectedUser!!.userId!!, item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.ACCEPT_FRIENDSHIP)
    }

    fun callToDelFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.deleteAFriend( loginAppManager.connectedUser!!.userId!!,item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.DEL_A_FRIEND)
    }
}