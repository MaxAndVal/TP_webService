package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
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
    @SerializedName("accepted")
    @Expose
    var accepted: Boolean? = null
    @SerializedName("user_image")
    @Expose
    var friendImage: String? = null

    constructor(inside: Parcel) {
        Log.d(TAG, "inside = $inside")
        this.userId = inside.readValue(Int::class.java.classLoader) as Int
        this.userName = inside.readValue(String::class.java.classLoader) as String
        when (inside.readInt()) {
            -1 -> accepted = null
            0 -> accepted = false
            1 -> accepted = true
        }
        this.friendImage = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(userName)
        if (accepted == null ) {
            dest.writeInt(-1)
        } else  {
            dest.writeInt(if (accepted!!) 1 else 0)
        }
        dest.writeValue(friendImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Friend(userId=$userId, userName=$userName, accepted=$accepted, friendImage=$friendImage)"
    }


}