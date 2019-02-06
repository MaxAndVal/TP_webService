package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.SocialActionsInterface
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_social.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SocialManager private constructor(private val context: Context){

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
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
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.LIST_OF_FRIENDS -> {
                            val social = response.body() as ListOfFriends
                            val code = social.code
                            val message = social.message
                            if (code == 200) {
                                val list = response.body() as ListOfFriends
                                socialFragment?.listOfFriends = list
                                socialFragment?.listOfActualFriends = list.friends?.filter { it.accepted == true}
                                socialFragment?.listOfPotentialFriends = list.friends?.filter { it.accepted == false}
                                Log.d(TAG, "List : "+socialFragment?.listOfFriends)
                                Log.d(TAG, "List actual : "+socialFragment?.listOfActualFriends)
                                Log.d(TAG, "List potential : "+socialFragment?.listOfPotentialFriends)

                                socialFragment?.updateDataSetList(socialFragment?.listOfActualFriends)
                            } else {
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            }
                            if (socialFragment != null && socialFragment!!.isVisible) {
                                socialFragment?.btn_friendsRequest?.text="voir les requetes en attente"
                                socialFragment?.btn_friendsRequest?.setOnClickListener { friendsRequest(link) }
                            }

                        }
                        RetrofitCallTypes.RESULT_FRIENDS_SEARCHING -> {
                            val social = response.body() as ListOfFriends
                            val code = social.code
                            val message = social.message
                            Log.d(TAG,social.toString())
                            if (code == 200) {
                                val list = response.body() as ListOfFriends
                                socialFragment?.resultFromSearch = list
                                socialFragment?.updateDataSetList(list.friends)
                            } else {
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RetrofitCallTypes.ACCEPTE_FRIENDSHIP ->{
                            val social = response.body() as ResponseFromApi
                            val code = social.code
                            val message = social.message
                            if(code == 200){
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            } else{
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RetrofitCallTypes.DEL_A_FRIEND -> {
                            //TODO: need implementation for rv
                            val result = response.body() as ResponseFromApi
                            val code = result.code
                            val message = result.message
                            if (code == 200) {
                                Toast.makeText(context, "delete : $message", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RetrofitCallTypes.ADD_A_FRIENDS -> {
                            //TODO: need implementation for rv
                            val result = response.body() as ResponseFromApi
                            val code = result.code
                            val message = result.message
                            if (code == 200) {
                                Toast.makeText(context, "added $message", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
                            }
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

    fun searchForFriends(friends: String?) {
        val resultCall = rickAndMortyAPI!!.searchForFriends(loginAppManager.connectedUser!!.userId!!, friends)
        callRetrofit(resultCall, RetrofitCallTypes.RESULT_FRIENDS_SEARCHING)
    }

    fun friendsRequest(link: SocialActionsInterface) {
        socialFragment?.updateDataSetList(socialFragment?.listOfPotentialFriends)
        socialFragment?.btn_friendsRequest?.text = "voir sa liste d'ami"
        socialFragment?.btn_friendsRequest?.setOnClickListener { getListOfFriends(loginAppManager.connectedUser!!.userId!!, link) }
    }

    fun callForAddFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.addAFriend( loginAppManager.connectedUser!!.userId!!, item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.ADD_A_FRIENDS)
    }

    fun callForValidateFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.validateAFriend( loginAppManager.connectedUser!!.userId!!, item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.ACCEPTE_FRIENDSHIP)
    }

    fun callToDelFriend(item: Friend) {
        val resultCall = rickAndMortyAPI!!.deleteAFriend( loginAppManager.connectedUser!!.userId!!,item.userId!!)
        callRetrofit(resultCall, RetrofitCallTypes.DEL_A_FRIEND)
    }
}