package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListOfFAQ : Parcelable {

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        inside.readList(this.FAQs, ListOfFAQ::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(FAQs)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ListOfFAQ(code=$code, message=$message, FAQs=$FAQs)"
    }


    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ListOfFAQ> = object : Parcelable.Creator<ListOfFAQ> {


            override fun createFromParcel(inside: Parcel): ListOfFAQ {
                return ListOfFAQ(inside)
            }

            override fun newArray(size: Int): Array<ListOfFAQ?> {
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
    @SerializedName("faq")
    @Expose
    var FAQs: List<FAQ>? = null



}