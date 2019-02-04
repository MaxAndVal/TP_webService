package com.example.lpiem.rickandmortyapp.View.Shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.ShopManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {

    private val shopManager = ShopManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        initViews()
    }

    private fun initViews() {
        iv_small_booster.setOnClickListener { buyBooster(40) }
        iv_medium_booster.setOnClickListener { buyBooster(100) }
        iv_big_booster.setOnClickListener { buyBooster(300) }
    }

    private fun buyBooster(cost: Int) {
        shopManager.buyBoosterIfEnable(cost)
    }

    override fun onBackPressed() {
        shopManager.cancelCall()

        super.onBackPressed()

    }
}
