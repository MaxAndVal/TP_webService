package com.example.lpiem.rickandmortyapp

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListOfDecks : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ListOfDecks> = object : Parcelable.Creator<ListOfDecks> {


            override fun createFromParcel(inside: Parcel): ListOfDecks {
                return ListOfDecks(inside)
            }

            override fun newArray(size: Int): Array<ListOfDecks?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("deck")
    @Expose
    var decks: List<Deck>? = null


    constructor(inside: Parcel) {
        inside.readList(this.decks, com.example.lpiem.rickandmortyapp.ListOfDecks::class.java.classLoader)

    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(decks)
    }

    override fun describeContents(): Int {
        return 0
    }



}
