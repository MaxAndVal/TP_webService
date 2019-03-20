package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

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

    private constructor(code: Int?, message: String?, cards: List<Card>?) {
        this.code = code
        this.message = message
        this.cards = cards
    }

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        inside.readList(this.cards, ListOfCards::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(cards)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun sortById(): ListOfCards {
        val cardsList = this.cards!!.sortedBy { it.cardId }
        return ListOfCards(this.code, this.message, cardsList)
    }

    override fun toString(): String {
        return "ListOfCards(code=$code, message=$message, cards=$cards)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListOfCards

        if (code != other.code) return false
        if (message != other.message) return false
        if (cards != other.cards) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code ?: 0
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (cards?.hashCode() ?: 0)
        return result
    }


}
