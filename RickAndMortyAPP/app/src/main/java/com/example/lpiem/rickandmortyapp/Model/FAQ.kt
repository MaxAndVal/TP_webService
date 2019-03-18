package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FAQ : Parcelable {

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FAQ> = object : Parcelable.Creator<FAQ> {


            override fun createFromParcel(inside: Parcel): FAQ {
                return FAQ(inside)
            }

            override fun newArray(size: Int): Array<FAQ?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("faq_question")
    @Expose
    var question: String? = null

    @SerializedName("faq_response")
    @Expose
    var response: String? = null

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(question)
        dest.writeValue(response)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "FAQ(question=$question, response=$response)"
    }

    constructor(inside: Parcel) {
        this.question = inside.readValue(String::class.java.classLoader) as String
        this.response = inside.readValue(String::class.java.classLoader) as String
    }



}