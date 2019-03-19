package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.FaqManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.FAQ
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQ_Fragment : androidx.fragment.app.Fragment(){

    private lateinit var faqManager: FaqManager
    private lateinit var faqObserver: Observer<List<FAQ>>

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        faqManager = FaqManager.getInstance(context!!)

        faqObserver = Observer {
            uploadListFAQ(it)
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
        faqManager.faqLoaderLiveData.observeForever(faqObserver)
        iv_closeFAQ.setOnClickListener { closeFAQ(this) }
    }

    private fun closeFAQ(fragment: Fragment) {
        faqManager.closeFaqLiveData.postValue(fragment)
    }

    override fun onDestroyView() {
        faqManager.faqLoaderLiveData.removeObserver(faqObserver)
        closeFAQ(this)
        super.onDestroyView()
    }

    private fun uploadListFAQ(listOfFaqFromSM: List<FAQ>) {
        rv_faq.adapter = FAQAdapter(listOfFaqFromSM)
        rv_faq.adapter?.notifyDataSetChanged()
    }

}
