package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseError : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ResponseError> = object : Parcelable.Creator<ResponseError> {


            override fun createFromParcel(inside: Parcel): ResponseError {
                return ResponseError(inside)
            }

            override fun newArray(size: Int): Array<ResponseError?> {
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

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
    }

    override fun describeContents(): Int {
        return 0
    }



}
