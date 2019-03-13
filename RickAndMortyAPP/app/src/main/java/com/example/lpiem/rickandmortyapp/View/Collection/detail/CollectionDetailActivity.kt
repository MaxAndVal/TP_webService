package com.example.lpiem.rickandmortyapp.View.Collection.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.DetailledCard
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : AppCompatActivity() {

    private lateinit var currentCard: Card
    private val detailManager = DetailCollectionManager.getInstance(this)
    private lateinit var cardDetailDisplayObserver: Observer<DetailledCard>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        cardDetailDisplayObserver = Observer { detailedCard ->
            card_detail_background.visibility = View.VISIBLE
            val status = detailedCard.status
            val species = detailedCard.species
            val gender = detailedCard.gender
            val origin = detailedCard.origin
            val location = detailedCard.location
            tv_detail_description.text = String.format(
                    getString(R.string.tv_description), status, species, gender, origin, location
            )
            tv_detail_card_name.text = detailedCard.name
            tv_detail_card_number.text = detailedCard.id.toString()
            Picasso.get()
                    .load(detailedCard.image)
                    .fit()
                    .centerInside()
                    .placeholder(getDrawable(R.drawable.vortex_ram)!!)
                    .into(iv_detail_card_image)
        }

        detailManager.cardDetailDisplay.observeForever(cardDetailDisplayObserver)
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("current_card")) {
            currentCard = intent.extras["current_card"] as Card
            displayCard()
        }

        if (detailManager.listOfNewCards != null && detailManager.listOfNewCards?.isNotEmpty()!!) {
            updateCard()
            card_detail_background.setOnClickListener { updateCard() }
        }
    }

    override fun onBackPressed() {
        detailManager.listOfNewCards = null
        detailManager.cardDetailDisplay.removeObserver(cardDetailDisplayObserver)
        super.onBackPressed()
    }

    private fun updateCard() {
        currentCard = detailManager.listOfNewCards!!.first()
        detailManager.listOfNewCards!!.removeAt(0)

        if (detailManager.listOfNewCards.isNullOrEmpty()) {
            card_detail_background.setOnClickListener { finish() }
        }
        displayCard()
    }

    private fun displayCard() {
        detailManager.getCardDetails(currentCard.cardId!!)
    }

}
