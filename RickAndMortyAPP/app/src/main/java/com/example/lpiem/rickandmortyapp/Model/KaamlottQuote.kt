package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class KaamlottQuote : Parcelable {

    @JvmField
    val CREATOR: Parcelable.Creator<ResponseFromApi> = object : Parcelable.Creator<ResponseFromApi> {

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
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("citation")
    @Expose
    var citation: String? = null
    @SerializedName("personnage")
    @Expose
    var personnage: String? = null

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        this.citation = inside.readValue(String::class.java.classLoader) as String
        this.personnage = inside.readValue(String::class.java.classLoader) as String

    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(citation)
        dest.writeValue(personnage)
    }

    override fun describeContents(): Int {
        return 0
    }


}
