package com.example.lpiem.rickandmortyapp.ViewModel.collection

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.Repository.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.Repository.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.Card
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.DetailledCard
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce

class DetailCollectionManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    var cardDetailDisplay = MutableLiveData<DetailledCard>()
    var listOfNewCards: MutableList<Card>? = null
    private var detailedCardLiveData = MutableLiveData<DetailledCard>()

    companion object : SingletonHolder<DetailCollectionManager, Context>(::DetailCollectionManager)

    fun getCardDetails(id: Int) {
        detailedCardLiveData = rickAndMortyAPI.getDetail(id)
        detailedCardLiveData.observeOnce(Observer {
            getCardDetailTreatment(it)
        })
    }

    private fun getCardDetailTreatment(response: DetailledCard) {
        val code = response.code
        if (code == SUCCESS) {
            cardDetailDisplay.postValue(response)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, response.message), Toast.LENGTH_SHORT).show()
        }
    }


}
