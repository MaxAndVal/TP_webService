package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
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
    @SerializedName("price")
    @Expose
    var price: Int? = null

    constructor(inside: Parcel?) {
        Log.d(TAG, "inside = $inside")
        this.userId = inside?.readValue(Int::class.java.classLoader) as Int?
        this.cardId = inside?.readValue(Int::class.java.classLoader) as Int?
        this.cardName = inside?.readValue(String::class.java.classLoader) as String?
        this.cardImage = inside?.readValue(String::class.java.classLoader) as String?
        this.amount = inside?.readValue(Int::class.java.classLoader) as Int?
        this.price = inside?.readValue(Int::class.java.classLoader) as Int?

    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(cardId)
        dest.writeValue(cardName)
        dest.writeValue(cardImage)
        dest.writeValue(amount)
        dest.writeValue(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Card(cardName= $cardName, " +
                "cardId= $cardId, " +
                "cardImage= $cardImage, " +
                "cardPrice= $price, " +
                "amount= $amount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        if (userId != other.userId) return false
        if (cardId != other.cardId) return false
        if (cardName != other.cardName) return false
        if (cardImage != other.cardImage) return false
        if (amount != other.amount) return false
        if (price != other.price) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId ?: 0
        result = 31 * result + (cardId ?: 0)
        result = 31 * result + (cardName?.hashCode() ?: 0)
        result = 31 * result + (cardImage?.hashCode() ?: 0)
        result = 31 * result + (amount ?: 0)
        result = 31 * result + (price ?: 0)
        return result
    }


}
