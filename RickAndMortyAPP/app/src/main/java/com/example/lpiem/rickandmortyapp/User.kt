package com.example.lpiem.rickandmortyapp

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {


            override fun createFromParcel(inside: Parcel): User {
                return User(inside)
            }

            override fun newArray(size: Int): Array<User?> {
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
    @SerializedName("user_email")
    @Expose
    var userEmail: String? = null
    @SerializedName("deck_To_Open")
    @Expose
    var deckToOpen: Int? = null

    constructor(inside: Parcel) {
        this.userId = inside.readValue(Int::class.java.classLoader) as Int
        this.userName = inside.readValue(String::class.java.classLoader) as String
        this.userEmail = inside.readValue(String::class.java.classLoader) as String
        this.deckToOpen = inside.readValue(Int::class.java.classLoader) as Int
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(userName)
        dest.writeValue(userEmail)
        dest.writeValue(deckToOpen)
    }

    override fun describeContents(): Int {
        return 0
    }



}
