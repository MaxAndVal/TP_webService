package com.example.lpiem.rickmorty_tcg.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result implements Parcelable
{

    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("results")
    @Expose
    private List<Character> character = null;
    public final static Parcelable.Creator<Result> CREATOR = new Creator<Result>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return (new Result[size]);
        }

    }
            ;

    protected Result(Parcel in) {
        this.info = ((Info) in.readValue((Info.class.getClassLoader())));
        in.readList(this.character, (com.example.lpiem.rickmorty_tcg.model.Result.class.getClassLoader()));
    }

    public Result() {
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Character> getResults() {
        return character;
    }

    public void setCharacter(List<Character> results) {
        this.character = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(info);
        dest.writeList(character);
    }

    public int describeContents() {
        return  0;
    }

}
