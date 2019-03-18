package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.lpiem.rickandmortyapp.View.TAG
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailledCard : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<DetailledCard> = object : Parcelable.Creator<DetailledCard> {


            override fun createFromParcel(inside: Parcel): DetailledCard {
                return DetailledCard(inside)
            }

            override fun newArray(size: Int): Array<DetailledCard?> {
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
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("species")
    @Expose
    var species: String? = null
    @SerializedName("gender")
    @Expose
    var gender: String? = null
    @SerializedName("origin")
    @Expose
    var origin: String? = null
    @SerializedName("location")
    @Expose
    var location: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null

    constructor(inside: Parcel) {
        Log.d(TAG, "inside = $inside")
        this.code = inside.readValue(Int::class.java.classLoader) as Int
        this.message = inside.readValue(String::class.java.classLoader) as String
        this.id = inside.readValue(Int::class.java.classLoader) as Int
        this.name = inside.readValue(String::class.java.classLoader) as String
        this.status = inside.readValue(String::class.java.classLoader) as String
        this.species = inside.readValue(String::class.java.classLoader) as String
        this.gender = inside.readValue(String::class.java.classLoader) as String
        this.origin = inside.readValue(String::class.java.classLoader) as String
        this.location = inside.readValue(String::class.java.classLoader) as String
        this.image = inside.readValue(String::class.java.classLoader) as String
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(code)
        dest.writeValue(message)
        dest.writeValue(id)
        dest.writeValue(name)
        dest.writeValue(status)
        dest.writeValue(species)
        dest.writeValue(gender)
        dest.writeValue(origin)
        dest.writeValue(location)
        dest.writeValue(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "DetailledCard(code=$code, message=$message, id=$id, name=$name, status=$status, species=$species, gender=$gender, origin=$origin, location=$location, image=$image)"
    }


}
