package com.example.lpiem.rickandmortyapp.Presenter.settings

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Presenter.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Settings.FAQ_Fragment
import kotlinx.android.synthetic.main.activity_bottom.*

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

    fun closeFAQ(fragment: Fragment) {

        var FAQFragment = (context as BottomActivity).supportFragmentManager
        val fragmentTransaction = FAQFragment.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        context.navigation.visibility = View.VISIBLE


    }

}