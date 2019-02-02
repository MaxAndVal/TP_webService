package com.example.lpiem.rickandmortyapp.Presenter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import okhttp3.ResponseBody
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Model.OnClickListenerInterface
import com.example.lpiem.rickandmortyapp.View.Social.SocialAdapter
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.DialogInterface
import android.os.Build
import android.view.View
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import kotlinx.android.synthetic.main.fragment_social.*


class SocialManager private constructor(private val context: Context):OnClickListenerInterface{

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var userId = -1
    private var userName = ""
    var socialFragment: SocialFragment? = null
    internal lateinit var recyclerView: RecyclerView

    companion object : SingletonHolder<SocialManager, Context>(::SocialManager)

    fun captureFragmentInstance(fragment: SocialFragment) {
        socialFragment = fragment
    }

    fun captureRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    @Synchronized
    fun getListOfFriends(userId: Int) {
        this.userId = userId
        val resultCall = rickAndMortyAPI!!.getListOfFriends(userId)
        callRetrofit(resultCall, RetrofitCallTypes.LIST_OF_FRIENDS)
        socialFragment!!.btn_friendsRequest.text="voir les requetes en attente"
        socialFragment!!.btn_friendsRequest.setOnClickListener { friendsRequest() }
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
                                    socialFragment?.listOfPotentialFriends = socialFragment?.listOfFriends?.friends!!.filter { it.accepted==false!! }
                                    socialFragment?.listOfActualFriends = socialFragment?.listOfFriends?.friends!!.filter { it.accepted!! }
                                    Log.d(TAG, "List : "+socialFragment?.listOfFriends)
                                    recyclerView.adapter = SocialAdapter(socialFragment?.listOfActualFriends!!, this@SocialManager)
                                    recyclerView.adapter?.notifyDataSetChanged()
                                }
                            } else {
                                //Toast.makeText(context, "code : $code, message ${kaamlott.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RetrofitCallTypes.RESULT_FRIENDS_SEARCHING -> {
                            var social = response.body() as ListOfFriends
                            var code = social.code
                            Log.d(TAG,social.toString())
                            if (code == 200) {
                                socialFragment?.resultFromSearch = response.body() as ListOfFriends
                                if (socialFragment?.resultFromSearch != null) {

                                    recyclerView.adapter = SocialAdapter(socialFragment?.resultFromSearch!!.friends!!, this@SocialManager)
                                    recyclerView.adapter?.notifyDataSetChanged()
                                } else {
                                    Toast.makeText(context, "code : $code, message ${social.message}", Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                        RetrofitCallTypes.ACCEPTE_FRIENDSHIP ->{
                            var social = response.body() as ResponseFromApi
                            if(social.code == 200){
                                Toast.makeText(context, "code : $social.code, message ${social.message}", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(context, "code : $social.code, message ${social.message}", Toast.LENGTH_SHORT).show()
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
        val resultCall = rickAndMortyAPI!!.searchForFriends(userId, friends)
        callRetrofit(resultCall, RetrofitCallTypes.RESULT_FRIENDS_SEARCHING)
    }
    override fun delFriends(item: Friend): Boolean {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(context)
        }
        builder.setTitle("Delete a friends")
                .setMessage("Are you sure you want to delete "+item.userName+" as a friend ?")
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    val resultCall = rickAndMortyAPI!!.delAfriends(socialFragment?.user!!.userId!!,item.userId!!)
                    callRetrofit(resultCall, RetrofitCallTypes.DEL_A_FRIEND)
                    recyclerView.adapter!!.notifyDataSetChanged()
                })
                .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                    // do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        return true}

    override fun addFriends(item: Friend) {
        Log.d(TAG, item.toString())
        if(item.accepted == null ){
            val resultCall = rickAndMortyAPI!!.addAfriends(socialFragment?.user!!.userId!!, item.userId!!)
            callRetrofit(resultCall, RetrofitCallTypes.ADD_A_FRIENDS)
        }else{
            val resultCall = rickAndMortyAPI!!.valideAFriends( socialFragment?.user!!.userId!!, item.userId!!)
            callRetrofit(resultCall, RetrofitCallTypes.ACCEPTE_FRIENDSHIP)
        }
        //TODO when it's done, go back to friend's List
        //TODO refresh
    }

    fun friendsRequest() {
        recyclerView.adapter = SocialAdapter(socialFragment?.listOfPotentialFriends!!, this@SocialManager)
        recyclerView.adapter?.notifyDataSetChanged()
        socialFragment!!.btn_friendsRequest.text = "voir sa liste d'ami"
        socialFragment!!.btn_friendsRequest.setOnClickListener { getListOfFriends(userId) }
    }

}