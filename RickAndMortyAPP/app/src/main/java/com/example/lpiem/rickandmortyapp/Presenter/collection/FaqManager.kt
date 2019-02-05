package com.example.lpiem.rickandmortyapp.Presenter.collection

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ListOfFAQ
import com.example.lpiem.rickandmortyapp.Model.SettingsOnClickInterface
import com.example.lpiem.rickandmortyapp.Presenter.SettingsManager
import com.example.lpiem.rickandmortyapp.Presenter.SingletonHolder
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Settings.FAQAdapter
import com.example.lpiem.rickandmortyapp.View.Settings.FAQ_Fragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FaqManager internal constructor(internal val context: Context) : SettingsOnClickInterface {
        override fun todo() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

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