package com.example.lpiem.rickandmortyapp.Manager.collection

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.DetailledCard
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CardDetailDisplay

class DetailCollectionManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private lateinit var cardDetailDisplay: CardDetailDisplay
    var listOfNewCards: MutableList<Card>? = null
    private var detailledCardLiveData = MutableLiveData<DetailledCard>()

    companion object : SingletonHolder<DetailCollectionManager, Context>(::DetailCollectionManager)

    fun getCardDetails(id: Int, link: CardDetailDisplay) {
        cardDetailDisplay = link
        detailledCardLiveData = rickAndMortyAPI.getDetail(id)
        detailledCardLiveData.observeOnce(Observer {
            getCardDetailTreatment(it)
        })
    }

    fun getCardDetailTreatment(response: DetailledCard) {
        val code = response.code
        if (code == SUCCESS) {
            cardDetailDisplay.displayResult(response)
        } else {
            Toast.makeText(context, "code : $code, message ${response.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
