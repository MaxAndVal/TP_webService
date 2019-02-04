package com.example.lpiem.rickandmortyapp.Model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Character : Parcelable, Serializable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Character> = object : Parcelable.Creator<Character> {


            override fun createFromParcel(inside: Parcel): Character {
                return Character(inside)
            }

            override fun newArray(size: Int): Array<Character?> {
                return arrayOfNulls(size)
            }

        }
    }

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
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("gender")
    @Expose
    var gender: String? = null
    @Expose
    var image: String? = null
    @SerializedName("episode")
    @Expose
    var episode: List<String>? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("created")
    @Expose
    var created: String? = null

    constructor(inside: Parcel) {
        this.id = inside.readValue(Int::class.java.classLoader) as Int
        this.name = inside.readValue(String::class.java.classLoader) as String
        this.status = inside.readValue(String::class.java.classLoader) as String
        this.species = inside.readValue(String::class.java.classLoader) as String
        this.type = inside.readValue(String::class.java.classLoader) as String
        this.gender = inside.readValue(String::class.java.classLoader) as String
        this.image = inside.readValue(String::class.java.classLoader) as String
        inside.readList(this.episode, java.lang.String::class.java.classLoader)
        this.url = inside.readValue(String::class.java.classLoader) as String
        this.created = inside.readValue(String::class.java.classLoader) as String
    }

        override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(id)
        dest.writeValue(name)
        dest.writeValue(status)
        dest.writeValue(species)
        dest.writeValue(type)
        dest.writeValue(gender)
        dest.writeValue(image)
        dest.writeList(episode)
        dest.writeValue(url)
        dest.writeValue(created)
    }

    override fun describeContents(): Int {
        return 0
    }



}