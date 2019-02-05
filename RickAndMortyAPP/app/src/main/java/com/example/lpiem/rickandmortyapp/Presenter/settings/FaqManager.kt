package com.example.lpiem.rickandmortyapp.Presenter.settings

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.FAQ
import com.example.lpiem.rickandmortyapp.Model.SettingsOnClickInterface
import com.example.lpiem.rickandmortyapp.Presenter.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.Settings.FAQ_Fragment

class FaqManager internal constructor(internal val context: Context)  {

        private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        var faqFragment: FAQ_Fragment? = null
        internal lateinit var recyclerView: RecyclerView

        companion object : SingletonHolder<FaqManager, Context>(::FaqManager)

        fun captureFragmentInstance(fragment: FAQ_Fragment) {
            faqFragment = fragment
        }

        fun captureRecyclerView(rv: RecyclerView) {
            recyclerView = rv
        }
}