package com.example.lpiem.rickandmortyapp

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListOfCards : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ListOfCards> = object : Parcelable.Creator<ListOfCards> {


            override fun createFromParcel(inside: Parcel): ListOfCards {
                return ListOfCards(inside)
            }

            override fun newArray(size: Int): Array<ListOfCards?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("deck")
    @Expose
    var cards: List<Card>? = null


    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        inside.readList(this.cards, com.example.lpiem.rickandmortyapp.ListOfCards::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(cards)
    }

    override fun describeContents(): Int {
        return 0
    }



}
