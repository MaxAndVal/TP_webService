package com.example.lpiem.rickandmortyapp.View.Social

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.Presenter.SocialManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_social.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SocialFragment : androidx.fragment.app.Fragment(), SocialActionsInterface {
    override fun openFriendsMArket(item: Friend) {
        val intent = Intent(context, MarketActivity::class.java)
        intent.putExtra("friend_id", item.userId)
        startActivity(intent)
    }


    private var param1: String? = null
    private var param2: String? = null

    private var rickAndMortyAPI: RickAndMortyAPI? = null
    var listOfFriends: ListOfFriends? = null
    private lateinit var socialManager: SocialManager
    private lateinit var loginAppManager: LoginAppManager
    var resultFromSearch: ListOfFriends? = null
    var listOfActualFriends: List<Friend>? = ArrayList()
    var listOfPotentialFriends: List<Friend>? = ArrayList()
    private var socialAdapter: SocialAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        loginAppManager = LoginAppManager.getInstance(context!!)

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        socialManager = SocialManager.getInstance(context!!)
        if (socialManager.socialFragment == null) {
            socialManager.captureFragmentInstance(this)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        socialManager.captureFragmentInstance(this)

        rv_social.layoutManager = LinearLayoutManager(context)
        socialManager.getListOfFriends(loginAppManager.connectedUser!!.userId!!, this)
        btn_searchFriends.setOnClickListener { socialManager.searchForFriends(sv_friends.query.toString()) }
        btn_friendsRequest.setOnClickListener { socialManager.friendsRequest(this) }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SocialFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    override fun addFriends(item: Friend) {
        Log.d(TAG, item.toString())
        if (item.accepted == null) {
            socialManager.callForAddFriend(item)
        } else {
            socialManager.callForValidateFriend(item)
        }
        //TODO when it's done, go back to friend's List
        //TODO refresh
    }

    override fun delFriends(item: Friend): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)

        builder.setTitle("Delete a friends")
                .setMessage("Are you sure you want to delete " + item.userName + " as a friend ?")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    socialManager.callToDelFriend(item)
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    // do nothing
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        return true
    }

    private fun updateRv() {
        rv_social.adapter?.notifyDataSetChanged()
    }

    fun updateDataSetList(list: List<Friend>?) {
        if (socialAdapter != null) {
            socialAdapter!!.updateDataSet(list)
            updateRv()
        } else {
            socialAdapter = SocialAdapter(listOfActualFriends, this)
            rv_social.adapter = socialAdapter
            updateRv()
        }
    }

}
