package com.example.lpiem.rickandmortyapp.View.Social;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.CollectionAdapter
import kotlinx.android.synthetic.main.social_item.view.*

class SocialAdapter(private val dataSet: ListOfFriends) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.social_item, parent, false)

        return SocialAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.friends!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.userId.text = dataSet.friends!![position].userId
        holder.userName.text = dataSet.friends!![position].userName
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.tv_userName
    }
}
