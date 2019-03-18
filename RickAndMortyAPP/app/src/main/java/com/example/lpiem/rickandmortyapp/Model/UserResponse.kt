package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class UserResponse : Parcelable {

    @JvmField val CREATOR: Parcelable.Creator<UserResponse> = object : Parcelable.Creator<UserResponse> {

        override fun createFromParcel(inside: Parcel): UserResponse {
            return UserResponse(inside)
        }

        override fun newArray(size: Int): Array<UserResponse?> {
            return arrayOfNulls(size)
        }

    }

    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("user")
    @Expose
    var user: User? = null
        private set

    constructor(inside: Parcel) {
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        this.user = inside.readValue(User::class.java.classLoader) as User
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(user)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "UserResponse( code=$code, message=$message, user= { ${user.toString()} })"
    }


}