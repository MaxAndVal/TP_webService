package com.example.lpiem.rickandmortyapp.View.Collection

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.Character
import com.example.lpiem.rickandmortyapp.Model.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.CollectionManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.fragment_collection.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATASET = "dataSet"
private const val ARG_PARAM2 = "param2"

class CollectionFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Parcelable? = null
    private var param2: String? = null
    private var rickAndMortyAPI: RickAndMortyAPI? = null
    var listOfCards: ListOfCards? = null
    private lateinit var collectionManager: CollectionManager
    private var user : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable<Character>(ARG_DATASET)
            param2 = it.getString(ARG_PARAM2)
        }
        user = activity?.intent?.getParcelableExtra("user")
        Log.d("userIntent : ", user.toString())

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        collectionManager = CollectionManager.getInstance(context!!)
        if (collectionManager.collectionFragment == null) {
            collectionManager.captureFragmentInstance(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_collection.layoutManager = GridLayoutManager(context, 3)
        collectionManager.captureRecyclerView(rv_collection)
        collectionManager.getListOfDecks(user)
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




}
