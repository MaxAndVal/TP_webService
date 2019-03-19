package com.example.lpiem.rickandmortyapp.Model.Games

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MemoryReward (@SerializedName("card_id") @Expose var cardId: Int, @SerializedName("card_name") @Expose var cardName: String)