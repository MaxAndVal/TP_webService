package com.example.lpiem.rickandmortyapp.View.Social;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.OnClickListenerInterface
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.social_item.view.*


class SocialAdapter(private val dataSet: List<Friend>, private val listener: OnClickListenerInterface) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.social_item, parent, false)
        return SocialAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = dataSet!![position]
        holder.userName.text = dataSet!![position].userName
        if (dataSet!![position].accepted !== null && dataSet!![position].accepted!!) {
            holder.ivIconFriends.setImageResource(R.drawable.store)
        }
        holder.ivIconFriends.setOnClickListener { listener.addFriends(item) }
        holder.userName.setOnLongClickListener { listener.delFriends(item) }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.tv_userName
        var ivIconFriends = view.iv_iconFriends

    }
}
