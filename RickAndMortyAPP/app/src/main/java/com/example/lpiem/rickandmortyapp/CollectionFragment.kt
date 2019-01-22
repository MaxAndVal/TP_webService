package com.example.lpiem.rickandmortyapp

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_collection.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATASET = "dataSet"
private const val ARG_PARAM2 = "param2"

class CollectionFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Parcelable? = null
    private var param2: String? = null
    private var rickAndMortyAPI: RickAndMortyAPI? = null
    private var dataset: ListOfDecks? = null
    private var user : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable<Character>(ARG_DATASET)
            param2 = it.getString(ARG_PARAM2)
        }
        user = activity?.intent?.getParcelableExtra<User>("user")
        Log.d("userIntent : ", user.toString())

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_collection.layoutManager = GridLayoutManager(context, 3)
        getListOfDecks()
    }

    companion object {
        @JvmStatic
        fun newInstance(dataset: Character, param2: String) =
                CollectionFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_DATASET, dataset)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    private fun <T> callRetrofit(call: Call<T>, i: Int) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if (i == 1) {
//                        fetchData(response as Response<Result>)
//                        if (nextPage < nbPages) {
//                            getAllCharacter()
//                        }
                    } else if (i == 2) {
//                        characterList = response.body() as List<Character>
//                        Log.d(TAG, "test character = ${character[0]}")
                    } else if (i == 3) {
                        val responseFromApi = response.body() as ResponseFromApi
                        Log.d(TAG, "responseFromApi: ${responseFromApi.code} / message: ${responseFromApi.message} / ${responseFromApi.results}")
                    } else if (i == 4) {
                        dataset = response.body() as ListOfDecks
                        //Log.d(TAG, "deck: ${listOfDecks.cards?.get(0)?.cardName} , ${listOfDecks.cards?.get(2)?.cardName}")
                        if (dataset != null) {
                            rv_collection.adapter = CollectionAdapter(dataset!!)
                            rv_collection.adapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }


    @Synchronized
    private fun getListOfDecks() {
        var userId = user?.userId?:-1
        Log.d("user id", userId.toString())
        val resultListDeck = rickAndMortyAPI!!.getListOfDecksById(userId)
        callRetrofit(resultListDeck, 4)
    }




}
