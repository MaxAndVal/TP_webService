package com.example.lpiem.rickandmortyapp.View.Collection.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.collection.CollectionManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.ListOfCards
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.RecyclerTouchListener
import com.example.lpiem.rickandmortyapp.View.Collection.detail.CollectionDetailActivity
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.android.synthetic.main.price_input.view.*

class CollectionFragment : androidx.fragment.app.Fragment() {

    var listOfCards: ListOfCards? = null
    private lateinit var collectionManager: CollectionManager
    private lateinit var loginAppManager: LoginAppManager
    private var user: User? = null
    private var adapter: CollectionAdapter? = null
    private var displayListLiveData = MutableLiveData<ListOfCards>()
    private lateinit var displayListObserver: Observer<ListOfCards>
    private lateinit var isAddCardSuccededObserver : Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginAppManager = LoginAppManager.getInstance(context!!)
        user = loginAppManager.connectedUser
        Log.d(TAG, "user : $user")

        collectionManager = CollectionManager.getInstance(context!!)
        collectionManager.captureFragmentInstance(this)

        displayListObserver = Observer { list ->
            if (adapter == null) {
                collection_loader.visibility = GONE
                rv_collection.visibility = VISIBLE
            }
            updateAdapter(list)
        }
        displayListLiveData.observeForever(displayListObserver)
        isAddCardSuccededObserver = Observer {
            if(it)onResume()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_collection.layoutManager = GridLayoutManager(context, 3)
        rv_collection.addOnItemTouchListener(RecyclerTouchListener(context!!, rv_collection, object : RecyclerTouchListener.ClickListener {

            override fun onClick(view: View, position: Int) {
                val detailIntent = Intent(context, CollectionDetailActivity::class.java)
                detailIntent.putExtra("current_card", (rv_collection.adapter as CollectionAdapter).getDataSet().cards?.get(position))
                context!!.startActivity(detailIntent)
            }

            override fun onLongClick(view: View, position: Int) {
                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                val card = (rv_collection.adapter as CollectionAdapter).getDataSet().cards?.get(position)
                val customView = layoutInflater.inflate(R.layout.price_input, null, false)
                builder.setView(customView)
                builder.setTitle(getString(R.string.sellThisCard))
                        .setMessage(getString(R.string.which_price_for_this_card))
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            val price = customView.et_price.text
                            var isValid = true
                            if (price.isBlank()) {
                                isValid = false
                            }
                            if (isValid) {
                                collectionManager.sellACard(user!!, card!!, price.toString().toInt(), displayListLiveData)
                            }
                            if (isValid) {
                                dialog.dismiss()
                            }
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            // do nothing
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }

        }))
    }

    private fun updateAdapter(list: ListOfCards) {
        if (adapter == null) {
            adapter = CollectionAdapter(list)
            rv_collection.adapter = adapter
            adapter!!.updateList(list)
        } else {
            adapter!!.updateList(list)
        }
    }

    override fun onDestroyView() {
        collectionManager.cancelCall()
        displayListLiveData.removeObserver(displayListObserver)
        //isAddCardSucceded.removeObserver()
        super.onDestroyView()
    }

    override fun onResume() {
        if (adapter == null) {
            rv_collection.visibility = GONE
            collection_loader.visibility = VISIBLE
        }
        collectionManager.getListOfDecks(user, displayListLiveData)
        super.onResume()
    }
}
