package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Card : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Card> = object : Parcelable.Creator<Card> {


            override fun createFromParcel(inside: Parcel): Card {
                return Card(inside)
            }

            override fun newArray(size: Int): Array<Card?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null
    @SerializedName("card_id")
    @Expose
    var cardId: Int? = null
    @SerializedName("card_name")
    @Expose
    var cardName: String? = null
    @SerializedName("card_image")
    @Expose
    var cardImage: String? = null
    @SerializedName("amount")
    @Expose
    var amount: Int? = null

    constructor(inside: Parcel) {
        Log.d(TAG, "inside = $inside")
        this.userId = inside.readValue(Int::class.java.classLoader) as Int
        this.cardId = inside.readValue(Int::class.java.classLoader) as Int
        this.cardName = inside.readValue(String::class.java.classLoader) as String
        this.cardImage = inside.readValue(String::class.java.classLoader) as String
        this.amount = inside.readValue(Int::class.java.classLoader) as Int
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(cardId)
        dest.writeValue(cardName)
        dest.writeValue(cardImage)
        dest.writeValue(amount)
    }

    override fun describeContents(): Int {
        return 0
    }



}
