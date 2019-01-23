package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Friend : Parcelable{

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Friend> = object : Parcelable.Creator<Friend> {


            override fun createFromParcel(inside: Parcel): Friend {
                return Friend(inside)
            }

            override fun newArray(size: Int): Array<Friend?> {
                return arrayOfNulls(size)
            }

        }
    }

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null
    @SerializedName("user_name")
    @Expose
    var userName: String? = null

    constructor(inside: Parcel) {
        Log.d(TAG, "inside = $inside")
        this.userId = inside.readValue(Int::class.java.classLoader) as Int
        this.userName = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

}
