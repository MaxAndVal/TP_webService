package com.example.lpiem.rickandmortyapp

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class ResponseFromApi : Parcelable {

    @JvmField val CREATOR: Parcelable.Creator<ResponseFromApi> = object : Parcelable.Creator<ResponseFromApi> {

        override fun createFromParcel(inside: Parcel): ResponseFromApi {
            return ResponseFromApi(inside)
        }

        override fun newArray(size: Int): Array<ResponseFromApi?> {
            return arrayOfNulls(size)
        }

    }

    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("success")
    @Expose
    var success: String? = null

    @SerializedName("response")
    @Expose
    var results: User? = null
        private set

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.success = inside.readValue(String::class.java.classLoader) as String
        this.results = inside.readValue(User::class.java.classLoader) as User
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(success)
        dest.writeValue(results)
    }

    override fun describeContents(): Int {
        return 0
    }



}