package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.View.Collection.CollectionAdapter
import com.example.lpiem.rickandmortyapp.View.Collection.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionManager private constructor(private val context: Context) {

    private var rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    var collectionFragment: CollectionFragment? = null
    private lateinit var recyclerView: RecyclerView
    private var fragment : CollectionFragment?=null


    init {

    }

    companion object : SingletonHolder<CollectionManager, Context>(::CollectionManager)

    fun captureFragmentInstance(fragment: CollectionFragment) {
        collectionFragment = fragment
    }

    fun captureRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }


//    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {
//
//        call.enqueue(object : Callback<T> {
//            override fun onResponse(call: Call<T>, response: Response<T>) {
//                if (response.isSuccessful) {
//                    when (type) {
//                        RetrofitCallTypes.RESPONSE_FROM_API -> {
//                            val responseFromApi = response.body() as ResponseFromApi
//                            Log.d(TAG, "responseFromApi: ${responseFromApi.code} / message: ${responseFromApi.message} / ${responseFromApi.results}")
//                        }
//                        RetrofitCallTypes.LIST_OF_CARDS -> {
//                            collectionFragment?.listOfCards = response.body() as ListOfCards
//                            if (collectionFragment?.listOfCards != null) {
//                                recyclerView.adapter = CollectionAdapter(collectionFragment?.listOfCards!!)
//                                recyclerView.adapter?.notifyDataSetChanged()
//                            }
//                        }
//                    }
//                } else {
//                    val responseError = response.errorBody() as ResponseBody
//                    Log.d(TAG, "error: ${responseError.string()}")
//                }
//
//            }
//
//            override fun onFailure(call: Call<T>, t: Throwable) {
//                Log.d(TAG, "fail : $t")
//            }
//        })
//
//    }


    @Synchronized
    fun getListOfDecks(user: User?) {
        val userId = user?.userId?:-1
        Log.d("user id", userId.toString())
        val resultListCard = rickAndMortyAPI!!.getListOfCardsById(userId)
        RickAndMortyRetrofitSingleton.callRetrofit(resultListCard, RetrofitCallTypes.LIST_OF_CARDS, context, fragment!!)
    }
    fun getFragment(fragment: CollectionFragment){
        this.fragment = fragment
    }}