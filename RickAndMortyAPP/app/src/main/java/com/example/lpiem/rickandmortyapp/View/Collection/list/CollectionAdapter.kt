package com.example.lpiem.rickandmortyapp.View.Collection.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*


class CollectionAdapter(private var dataSet: ListOfCards): RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardAmount.text = "X ${dataSet.cards!![position].amount} "
        holder.cardId.text = dataSet.cards!![position].cardId.toString()
        holder.title.text = dataSet.cards!![position].cardName.toString()
        Picasso.get().load(dataSet.cards!![position].cardImage).into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (dataSet.cards != null) dataSet.cards!!.size else 0
    }

    fun getDataSet(): ListOfCards {
        return dataSet
    }

    fun updateList(newList: ListOfCards) {
        dataSet = dataSet
        val diffResult = DiffUtil.calculateDiff(DiffUtilCollection(dataSet, newList))
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateDataSet(newList: ListOfCards){
        dataSet = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.iv_card!!
        val cardId = view.tv_card_number!!
        val cardAmount = view.tv_card_amount!!
        val title = view.tv_title!!
    }


}