package com.example.lpiem.rickandmortyapp.Model

import android.graphics.drawable.Drawable
import android.widget.ImageView

data class Tile(var image: Drawable, var refName: String, var placeHolder: Int, var tileView: ImageView, var tapped: Boolean, var cardId: Int)