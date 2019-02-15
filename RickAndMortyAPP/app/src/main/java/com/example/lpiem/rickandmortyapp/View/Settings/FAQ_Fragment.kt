package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lpiem.rickandmortyapp.Model.FAQ
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.settings.FaqManager
import com.example.lpiem.rickandmortyapp.Presenter.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQ_Fragment : androidx.fragment.app.Fragment(){

    private lateinit var faqManager: FaqManager
    var listOfFAQ : List<FAQ>?=null

    internal var user: User? = null

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        faqManager = FaqManager.getInstance(context!!)
        if (faqManager.faqFragment == null) {
            faqManager.captureFragmentInstance(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_faq.layoutManager = LinearLayoutManager(context)
        faqManager.captureRecyclerView(rv_faq)
        iv_closeFAQ.setOnClickListener { faqManager.closeFAQ(this) }
    }

    override fun onDestroyView() {
        faqManager.closeFAQ(this)
        super.onDestroyView()
    }

}
