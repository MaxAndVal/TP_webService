package com.example.lpiem.rickandmortyapp.View.Social

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfFriends
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.SocialManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.fragment_social.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SocialFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var rickAndMortyAPI: RickAndMortyAPI?=null
    var listOfFriends: ListOfFriends? = null
    private lateinit var socialManager: SocialManager
    private var user : User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        user = activity?.intent?.getParcelableExtra("user")
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        socialManager = SocialManager.getInstance(context!!)
        if (socialManager.socialFragment == null) {
            socialManager.captureFragmentInstance(this)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SocialFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_social.layoutManager = LinearLayoutManager(context)
        socialManager.captureRecyclerView(rv_social)
        var userId= if(user!=null)user?.userId else -1
        socialManager.getListOfFriends(userId!!)
    }
}
