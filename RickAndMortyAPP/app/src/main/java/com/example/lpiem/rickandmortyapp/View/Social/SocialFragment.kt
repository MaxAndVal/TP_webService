package com.example.lpiem.rickandmortyapp.View.Social

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.SocialManager
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel.LIST_OF_FRIENDS
import com.example.lpiem.rickandmortyapp.Model.SocialListLabel.LIST_OF_FRIENDS_REQUESTS
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_social.*



class SocialFragment : androidx.fragment.app.Fragment(), SocialActionsInterface {

    private lateinit var socialManager: SocialManager
    private lateinit var loginAppManager: LoginAppManager
    private lateinit var updateListObserver: Observer<List<Friend>?>
    private lateinit var changeBtnActionObserver: Observer<SocialListLabel>
    private var socialAdapter: SocialAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginAppManager = LoginAppManager.getInstance(context!!)
        socialManager = SocialManager.getInstance(context!!)

        updateListObserver = Observer {
            updateDataSetList(it)
        }

        changeBtnActionObserver = Observer { btnLabel ->
            if (btnLabel == LIST_OF_FRIENDS) {
                tv_list_title?.text = getString(R.string.list_of_friends_title)
                btn_friendsRequest?.text = getString(R.string.btn_pending_friend_requests)
                btn_friendsRequest?.setOnClickListener {
                    socialManager.friendsRequest(this)
                }
            } else if (btnLabel == LIST_OF_FRIENDS_REQUESTS) {
                tv_list_title?.text = getString(R.string.requests_of_friends_title)
                btn_friendsRequest?.text = getString(R.string.btn_list_of_friends)
                btn_friendsRequest?.setOnClickListener {
                    socialManager.getListOfFriends(loginAppManager.connectedUser!!.userId!!, this)
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        socialManager.updateListLiveData.observeForever(updateListObserver)
        socialManager.changeBtnActionLiveData.observeForever(changeBtnActionObserver)

        rv_social.layoutManager = LinearLayoutManager(context)
        socialManager.getListOfFriends(loginAppManager.connectedUser!!.userId!!, this)
        btn_searchFriends.setOnClickListener { socialManager.searchForFriends(sv_friends.query.toString()) }
        btn_friendsRequest.setOnClickListener { socialManager.friendsRequest(this) }
    }

    override fun addFriends(item: Friend) {
        Log.d(TAG, item.toString())
        if (item.accepted == null) {
            socialManager.callForAddFriend(item)
        } else {
            socialManager.callForValidateFriend(item)
        }
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

    private fun updateDataSetList(list: List<Friend>?) {
        if (rv_social != null) {
            if (socialAdapter != null) {
                socialAdapter!!.updateDataSet(list)
                updateRv()
            } else {
                socialAdapter = SocialAdapter(socialManager.listOfActualFriends, this)
                rv_social.adapter = socialAdapter
                updateRv()
            }
        }

    }

    override fun openFriendsMArket(item: Friend) {
        val intent = Intent(context, MarketActivity::class.java)
        intent.putExtra("friend_id", item.userId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        socialManager.updateListLiveData.removeObserver(updateListObserver)
        socialManager.changeBtnActionLiveData.removeObserver(changeBtnActionObserver)
        super.onDestroyView()
    }
}
