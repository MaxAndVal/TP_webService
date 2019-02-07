package com.example.lpiem.rickandmortyapp.View.Social;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.Friend
import com.example.lpiem.rickandmortyapp.Model.SocialActionsInterface
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.social_item.view.*


class SocialAdapter(private var dataSet: List<Friend>?, private val listener: SocialActionsInterface) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

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
        holder.userName.text = item.userName
        if (item.accepted !== null && item.accepted!!) {
            holder.ivIconFriends.setImageResource(R.drawable.ic_store_24dp)
        }
        holder.ivIconFriends.setOnClickListener { listener.addFriends(item) }
        holder.userName.setOnLongClickListener { listener.delFriends(item) }
    }

    fun updateDataSet(newList: List<Friend>?) {
        dataSet = newList
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.tv_userName
        var ivIconFriends = view.iv_iconFriends
    }
}
