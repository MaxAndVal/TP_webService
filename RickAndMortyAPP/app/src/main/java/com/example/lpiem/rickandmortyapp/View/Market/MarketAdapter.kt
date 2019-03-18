package com.example.lpiem.rickandmortyapp.View.Market

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.market_item.view.*

class MarketAdapter (internal var dataSet: ListOfCards): RecyclerView.Adapter<MarketAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardPrice.text = "$ ${dataSet.cards!![position].price}  "
        holder.cardId.text = dataSet.cards!![position].cardId.toString()
        holder.title.text = dataSet.cards!![position].cardName.toString()
        Picasso.get().load(dataSet.cards!![position].cardImage).into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.market_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataSet.cards!!.size
    }

    fun getDataSet(): ListOfCards {
        return dataSet
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.iv_card!!
        val cardId = view.tv_card_number!!
        val cardPrice = view.tv_card_price!!
        val title = view.tv_title!!
    }
}