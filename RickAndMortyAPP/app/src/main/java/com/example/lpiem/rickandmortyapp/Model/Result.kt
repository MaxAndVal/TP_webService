package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result : Parcelable {

    @JvmField val CREATOR: Parcelable.Creator<Result> = object : Parcelable.Creator<Result> {

        override fun createFromParcel(inside: Parcel): Result {
            return Result(inside)
        }

        override fun newArray(size: Int): Array<Result?> {
            return arrayOfNulls(size)
        }

    }

    @SerializedName("info")
    @Expose
    var info: Info? = null
    @SerializedName("results")
    @Expose
    var results: List<Character>? = null
        private set

    constructor(inside: Parcel) {
        this.info = inside.readValue(Info::class.java.classLoader) as Info
        inside.readList(this.results, Result::class.java.classLoader)
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(info)
        dest.writeList(results)
    }

    override fun describeContents(): Int {
        return 0
    }

}
