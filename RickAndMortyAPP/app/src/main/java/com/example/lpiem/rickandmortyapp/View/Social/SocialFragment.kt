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
import com.example.lpiem.rickandmortyapp.Model.SocialListenerAction
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Market.MarketActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.fragment_social.*

class SocialFragment : androidx.fragment.app.Fragment() {

    private lateinit var socialManager: SocialManager
    private lateinit var loginAppManager: LoginAppManager
    private lateinit var updateListObserver: Observer<List<Friend>?>
    private lateinit var changeBtnActionObserver: Observer<SocialListLabel>
    private lateinit var adapterTouchItemObserver: Observer<Pair<SocialListenerAction, Friend>>
    private var socialAdapter: SocialAdapter? = null

//TODO(test before commit)
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
                    socialManager.friendsRequest()
                }
            } else if (btnLabel == LIST_OF_FRIENDS_REQUESTS) {
                tv_list_title?.text = getString(R.string.requests_of_friends_title)
                btn_friendsRequest?.text = getString(R.string.btn_list_of_friends)
                btn_friendsRequest?.setOnClickListener {
                    socialManager.getListOfFriends(loginAppManager.connectedUser!!.userId!!)
                }
            }
        }

        adapterTouchItemObserver = Observer { item ->
            when (item.first) {
                SocialListenerAction.ADD_FRIENDS -> {
                    Log.d(TAG, item.toString())
                    if (item.second.accepted == null) {
                        socialManager.callForAddFriend(item.second)
                    } else {
                        socialManager.callForValidateFriend(item.second)
                    }
                }
                SocialListenerAction.DEL_FRIENDS -> {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_NoActionBar)

                    builder.setTitle(getString(R.string.delete_a_friend))
                            .setMessage(String.format(getString(R.string.sure_to_delete_friend), item.second.userName))
                            .setPositiveButton(android.R.string.yes) { dialog, which ->
                                socialManager.callToDelFriend(item.second)
                            }
                            .setNegativeButton(android.R.string.no) { dialog, which ->
                                // do nothing
                            }
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()

                }
                SocialListenerAction.OPEN_FRIEND_MARKET -> {
                    val intent = Intent(context, MarketActivity::class.java)
                    intent.putExtra("friend_id", item.second.userId)
                    startActivity(intent)
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
        socialManager.getListOfFriends(loginAppManager.connectedUser!!.userId!!)
        btn_searchFriends.setOnClickListener { socialManager.searchForFriends(sv_friends.query.toString()) }
        btn_friendsRequest.setOnClickListener { socialManager.friendsRequest() }
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
                socialAdapter = SocialAdapter(socialManager.listOfActualFriends)
                socialAdapter?.liveDataListener?.observeForever(adapterTouchItemObserver)
                rv_social.adapter = socialAdapter
                updateRv()
            }
        }

    }



    override fun onDestroyView() {
        socialManager.updateListLiveData.removeObserver(updateListObserver)
        socialManager.changeBtnActionLiveData.removeObserver(changeBtnActionObserver)
        socialAdapter?.liveDataListener?.removeObserver(adapterTouchItemObserver)
        super.onDestroyView()
    }
}
