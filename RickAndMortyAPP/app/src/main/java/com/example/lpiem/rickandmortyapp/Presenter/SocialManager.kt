package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.View.Social.SocialAdapter
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SocialManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var userId = -1
    private var userName = ""
    var socialFragment: SocialFragment? = null
    private lateinit var recyclerView: RecyclerView

    companion object : SingletonHolder<SocialManager, Context>(::SocialManager)

    fun captureFragmentInstance(fragment: SocialFragment) {
        socialFragment = fragment
    }

    fun captureRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    @Synchronized
    fun getListOfFriends(userId:Int){
        val resultCall = rickAndMortyAPI!!.getListOfFriends(userId)
        callRetrofit(resultCall, RetrofitCallTypes.LIST_OF_FRIENDS)
    }
    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.LIST_OF_FRIENDS -> {
                            var social = response.body() as ListOfFriends
                            var code = social.code
                            if (code == 200) {
                                socialFragment?.listOfFriends = response.body() as ListOfFriends
                                if (socialFragment?.listOfFriends != null) {
                                    recyclerView.adapter = SocialAdapter(socialFragment?.listOfFriends!!)
                                    recyclerView.adapter?.notifyDataSetChanged()
                                }
                            } else {
                                //Toast.makeText(context, "code : $code, message ${kaamlott.message}", Toast.LENGTH_SHORT).show()
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

}