package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Wallet : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Wallet> = object : Parcelable.Creator<Wallet> {


            override fun createFromParcel(inside: Parcel): Wallet {
                return Wallet(inside)
            }

            override fun newArray(size: Int): Array<Wallet?> {
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
    @SerializedName("wallet")
    @Expose
    var wallet: Int? = null


    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        this.wallet = inside.readValue(Int::class.java.classLoader) as Int
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(wallet)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Wallet(code=$code, message=$message, wallet=$wallet)"
    }


}