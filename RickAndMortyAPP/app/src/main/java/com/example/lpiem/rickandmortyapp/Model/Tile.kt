package com.example.lpiem.rickandmortyapp.Model

import android.widget.ImageView

data class Tile(var image: Int, var refId: Int, var placeHolder: Int, var tileView: ImageView, var tapped: Boolean)