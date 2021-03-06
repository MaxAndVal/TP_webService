package com.example.lpiem.rickandmortyapp.View.BackActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.CardBooster
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.ViewModel.BackActivity.ShopManager
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {

    private var shopManager = ShopManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        initViews()
    }

    private fun initViews() {
        tv_small_booster.text = String.format(getString(R.string.booster_amount), CardBooster.LITTLE.amount)
        tv_medium_booster.text = String.format(getString(R.string.booster_amount), CardBooster.MEDIUM.amount)
        tv_big_booster.text = String.format(getString(R.string.booster_amount), CardBooster.LARGE.amount)
        tv_cost_small_booster.text = CardBooster.LITTLE.cost.toString()
        tv_cost_medium_booster.text = CardBooster.MEDIUM.cost.toString()
        tv_cost_big_booster.text = CardBooster.LARGE.cost.toString()
        iv_small_booster.setOnClickListener {
            if (!shopManager.transactionInProgress) {
                buyBooster(CardBooster.LITTLE.cost)
                shopManager.transactionInProgress = true
            }
        }
        iv_medium_booster.setOnClickListener {
            if (!shopManager.transactionInProgress) {
                buyBooster(CardBooster.MEDIUM.cost)
                shopManager.transactionInProgress = true
            }
        }
        iv_big_booster.setOnClickListener {
            if (!shopManager.transactionInProgress) {
                buyBooster(CardBooster.LARGE.cost)
                shopManager.transactionInProgress = true
            }
        }
    }

    private fun buyBooster(cost: Int) {
        shopManager.buyBoosterIfEnable(cost)
    }

    override fun onBackPressed() {
        shopManager.cancelCall()
        super.onBackPressed()

    }
}
