package com.example.lpiem.rickandmortyapp.View.Social;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.SocialListenerAction
import com.example.lpiem.rickandmortyapp.Model.SocialListenerAction.*
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.TAG
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.social_item.view.*


class SocialAdapter(private var dataSet: List<Friend>?) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    var liveDataListener = MediatorLiveData<Pair<SocialListenerAction, Friend>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.social_item, parent, false)
        return SocialAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (dataSet != null) dataSet!!.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataSet!![position]
        Log.d(TAG, "friend image : ${item.friendImage}")
        holder.userName.text = item.userName
        Picasso.get().load(item.friendImage)
                .placeholder(R.drawable.ic_person_white_24dp)
                .fit().centerInside()
                .into(holder.userIcon)
        if (item.accepted !== null && item.accepted!!) {
            holder.ivIconFriends.setImageResource(R.drawable.ic_store_24dp)
            holder.ivIconFriends.setOnClickListener {
                liveDataListener.postValue(Pair(OPEN_FRIEND_MARKET, item))
            }

        } else {
            holder.ivIconFriends.setImageResource(R.drawable.ic_add_24dp)
            holder.ivIconFriends.setOnClickListener {
                liveDataListener.postValue(Pair(ADD_FRIENDS, item))
            }
        }

        holder.userName.setOnLongClickListener {
            liveDataListener.postValue(Pair(DEL_FRIENDS, item))
            true
        }
    }

    fun updateDataSet(newList: List<Friend>?) {
        dataSet = newList
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.tv_userName as TextView
        var ivIconFriends = view.iv_iconFriends as ImageView
        val userIcon = view.iv_avatarFriends as ImageView
    }
}
