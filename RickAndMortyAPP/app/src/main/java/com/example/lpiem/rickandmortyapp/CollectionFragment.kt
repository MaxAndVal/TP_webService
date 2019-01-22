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
    private var mDataset: ListOfCards? = null
    private lateinit var galleryManager: GalleryManager
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
        galleryManager = GalleryManager.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_collection.layoutManager = GridLayoutManager(context, 3)
        getListOfDecks()
        //FIXME: just a test for the Manager
        galleryManager.doStuff()
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


    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if (type == RetrofitCallTypes.RESPONSE_FROM_API) {
                        val responseFromApi = response.body() as ResponseFromApi
                        Log.d(TAG, "responseFromApi: ${responseFromApi.code} / message: ${responseFromApi.message} / ${responseFromApi.results}")
                    } else if (type == RetrofitCallTypes.LIST_OF_CARDS) {
                        mDataset = response.body() as ListOfCards
                        //Log.d(TAG, "deck: ${listOfDecks.cards?.get(0)?.cardName} , ${listOfDecks.cards?.get(2)?.cardName}")
                        if (mDataset != null) {
                            rv_collection.adapter = CollectionAdapter(mDataset!!)
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
        val resultListCard = rickAndMortyAPI!!.getListOfCardsById(userId)
        callRetrofit(resultListCard, RetrofitCallTypes.LIST_OF_CARDS)
    }




}
