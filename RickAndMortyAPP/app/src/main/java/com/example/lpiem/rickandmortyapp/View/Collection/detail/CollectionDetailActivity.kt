package com.example.lpiem.rickandmortyapp.View.Collection.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.DetailledCard
import com.example.lpiem.rickandmortyapp.Presenter.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : AppCompatActivity(), CardDetailDisplay {



    private lateinit var currentCard: Card
    private val detailManager = DetailCollectionManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("current_card")) {
            currentCard = intent.extras["current_card"] as Card
            displayCard()
        }
    }

    private fun displayCard() {
        detailManager.getCardDetails(currentCard.cardId!!, this)
    }

    override fun displayResult(details: DetailledCard) {
        card_detail_background.visibility = View.VISIBLE
        val status = details.status
        val species = details.species
        val gender = details.gender
        val origin = details.origin
        val location = details.location
        tv_detail_description.text = String.format(getString(R.string.tv_description),status, species, gender, origin, location )
        tv_detail_card_name.text = details.name
        tv_detail_card_number.text = details.id.toString()
        Picasso.get()
                .load(details.image)
                .fit()
                .centerInside()
                .placeholder(getDrawable(R.drawable.abc_btn_check_material))
                .into(iv_detail_card_image)
    }

}
