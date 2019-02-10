package com.example.lpiem.rickandmortyapp.Presenter.collection

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.GET_CARD_DETAILS
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Card
import com.example.lpiem.rickandmortyapp.Model.DetailledCard
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CardDetailDisplay
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailCollectionManager private constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private lateinit var cardDetailDisplay: CardDetailDisplay
    var listOfnewCards: MutableList<Card>? = null

    companion object : SingletonHolder<DetailCollectionManager, Context>(::DetailCollectionManager)


    fun getCardDetails(id: Int, link: CardDetailDisplay) {
        cardDetailDisplay = link
        val cardDetails = rickAndMortyAPI!!.getCardDetails(id)
        callRetrofit(cardDetails, GET_CARD_DETAILS)
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    val result = response.body()
                    when (type) {
                        GET_CARD_DETAILS -> {
                            getCardDetailTreatment(result as DetailledCard)
                        }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
                Toast.makeText(context, "network problem : $t", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getCardDetailTreatment(response: DetailledCard) {
        val code = response.code
        if (code == 200) {
            cardDetailDisplay.displayResult(response)
        } else {
            Toast.makeText(context, "code : $code, message ${response.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
