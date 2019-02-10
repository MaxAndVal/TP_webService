package com.example.lpiem.rickandmortyapp.View.Collection.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.DetailledCard
import com.example.lpiem.rickandmortyapp.Presenter.OpenDeckManager
import com.example.lpiem.rickandmortyapp.Presenter.collection.DetailCollectionManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.OpenDeckActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : AppCompatActivity(), CardDetailDisplay {



    private lateinit var currentCard: Card
    private var cardInDeck: Int? = null
    private val detailManager = DetailCollectionManager.getInstance(this)
    private val deckOpenDeckManager = OpenDeckManager.getInstance(this)


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

        if (detailManager.listOfnewCards != null && detailManager.listOfnewCards?.isNotEmpty()!!) {
            updateCard()
            card_detail_background.setOnClickListener { updateCard() }
        }
    }

    override fun onBackPressed() {
        detailManager.listOfnewCards = null
        super.onBackPressed()
    }

    private fun updateCard() {
        currentCard = detailManager.listOfnewCards!!.first()
        detailManager.listOfnewCards!!.removeAt(0)

        if(detailManager.listOfnewCards.isNullOrEmpty()){
            card_detail_background.setOnClickListener { finish() }
        }
        displayCard()
    }

    private fun displayCard() {
        detailManager.getCardDetails(currentCard.cardId!!, this)
    }

    override fun displayResult(detailledCard: DetailledCard) {
        card_detail_background.visibility = View.VISIBLE
        val status = detailledCard.status
        val species = detailledCard.species
        val gender = detailledCard.gender
        val origin = detailledCard.origin
        val location = detailledCard.location
        tv_detail_description.text = String.format(getString(R.string.tv_description),status, species, gender, origin, location )
        tv_detail_card_name.text = detailledCard.name
        tv_detail_card_number.text = detailledCard.id.toString()
        Picasso.get()
                .load(detailledCard.image)
                .fit()
                .centerInside()
                .placeholder(getDrawable(R.drawable.vortex_ram))
                .into(iv_detail_card_image)
    }

}
