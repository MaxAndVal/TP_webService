package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListOfFriends : Parcelable {

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        inside.readList(this.friends, ListOfFriends::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(friends)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ListOfFriends(code=$code, message=$message, friends=$friends)"
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ListOfFriends> = object : Parcelable.Creator<ListOfFriends> {


            override fun createFromParcel(inside: Parcel): ListOfFriends {
                return ListOfFriends(inside)
            }

            override fun newArray(size: Int): Array<ListOfFriends?> {
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
    @SerializedName("friends")
    @Expose
    var friends: List<Friend>? = null

}