package com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
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
    @SerializedName("deckToOpen")
    @Expose
    var deckToOpen: Int? = null
    @SerializedName("user_wallet")
    @Expose
    var userWallet: Int? = null
    @SerializedName("user_last_game")
    @Expose
    var userLastGame: String? = null
    @SerializedName("user_last_memory")
    @Expose
    var userLastMemory: String? = null
    @SerializedName("user_image")
    @Expose
    var userImage: String? = null
    @SerializedName("external_id")
    @Expose
    var externalId: String? = null
    @SerializedName("session_token")
    @Expose
    var sessionToken: String? = null

    constructor(inside: Parcel) {
        Log.d(TAG, "inside = $inside")
        this.userId = inside.readValue(Int::class.java.classLoader) as Int
        this.userName = inside.readValue(String::class.java.classLoader) as String
        this.userEmail = inside.readValue(String::class.java.classLoader) as String
        this.deckToOpen = inside.readValue(Int::class.java.classLoader) as Int
        this.userWallet = inside.readValue(Int::class.java.classLoader) as Int
        this.userLastGame = inside.readValue(String::class.java.classLoader) as String
        this.userLastMemory = inside.readValue(String::class.java.classLoader) as String
        this.userImage = inside.readValue(String::class.java.classLoader) as String
        this.externalId = inside.readValue(String::class.java.classLoader) as String
        this.sessionToken = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeValue(userName)
        dest.writeValue(userEmail)
        dest.writeValue(deckToOpen)
        dest.writeValue(userWallet)
        dest.writeValue(userLastGame)
        dest.writeValue(userLastMemory)
        dest.writeValue(userImage)
        dest.writeValue(externalId)
        dest.writeValue(sessionToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "User(userId=$userId," +
                " userName=$userName, " +
                "userEmail=$userEmail, " +
                "deckToOpen=$deckToOpen, " +
                "userWallet=$userWallet, " +
                "userLastGame=$userLastGame, " +
                "userLastMemory=$userLastMemory, " +
                "userImage=$userImage, " +
                "externalId=$externalId, " +
                "sessionToken=$sessionToken)"
    }


}
