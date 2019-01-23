package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Info : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Info> = object : Parcelable.Creator<Info> {


            override fun createFromParcel(inside: Parcel): Info {
                return Info(inside)
            }

            override fun newArray(size: Int): Array<Info?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("count")
    @Expose
    var count: Int? = null
    @SerializedName("pages")
    @Expose
    var pages: Int? = null
    @SerializedName("next")
    @Expose
    var next: String? = null
    @SerializedName("prev")
    @Expose
    var prev: String? = null

    constructor(inside: Parcel) {
        this.count = inside.readValue(Int::class.java.classLoader) as Int
        this.pages = inside.readValue(Int::class.java.classLoader) as Int
        this.next = inside.readValue(String::class.java.classLoader) as String
        this.prev = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(count)
        dest.writeValue(pages)
        dest.writeValue(next)
        dest.writeValue(prev)
    }

    override fun describeContents(): Int {
        return 0
    }



}

