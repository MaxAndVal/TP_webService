package com.example.lpiem.rickandmortyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*

class CollectionAdapter(private val dataSet: ListOfDecks): RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardAmount.text = "X ${dataSet.cards!![position].amount} "
        holder.cardId.text = dataSet.cards!![position].cardId.toString()
        holder.title.text = dataSet.cards!![position].cardName.toString()
        Picasso.get().load(dataSet.cards!![position].cardImage).into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.cards!!.size
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.iv_card
        val cardId = view.tv_card_number
        val cardAmount = view.tv_card_amount
        val title = view.tv_title
    }
}