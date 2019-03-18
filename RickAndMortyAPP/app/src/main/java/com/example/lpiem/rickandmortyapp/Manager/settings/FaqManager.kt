package com.example.lpiem.rickandmortyapp.Manager.settings

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder

class FaqManager internal constructor(internal val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    internal lateinit var recyclerView: RecyclerView
    var closeFaqLiveData = MutableLiveData<Fragment>()


    companion object : SingletonHolder<FaqManager, Context>(::FaqManager)

    fun captureRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }


}