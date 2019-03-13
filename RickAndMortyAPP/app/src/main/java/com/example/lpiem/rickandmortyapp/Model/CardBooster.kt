package com.example.lpiem.rickandmortyapp.Model

enum class CardBooster(var cost: Int, var amount: Int) {
    LITTLE(40, 1),
    MEDIUM(100, 3),
    LARGE(300,10)
}