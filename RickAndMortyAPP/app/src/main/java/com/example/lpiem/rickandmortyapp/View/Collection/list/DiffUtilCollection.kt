package com.example.lpiem.rickandmortyapp.View.Collection.list

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.example.lpiem.rickandmortyapp.Model.ListOfCards


class DiffUtilCollection(private var newListOfCards: ListOfCards, private var oldListOfCards: ListOfCards) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return if (oldListOfCards.cards != null) oldListOfCards.cards!!.size else 0
    }

    override fun getNewListSize(): Int {
        return if (newListOfCards.cards != null) newListOfCards.cards!!.size else 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldListOfCards.cards != null && newListOfCards.cards != null) {
            oldListOfCards.cards!![oldItemPosition].cardId == newListOfCards.cards!![newItemPosition].cardId
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldListOfCards.cards != null && newListOfCards.cards != null) {
            oldListOfCards.cards!![oldItemPosition] == newListOfCards.cards!![newItemPosition]
        } else {
            return false
        }
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}