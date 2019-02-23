package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lpiem.rickandmortyapp.Manager.settings.FaqManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQ_Fragment : androidx.fragment.app.Fragment(){

    private lateinit var faqManager: FaqManager

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        faqManager = FaqManager.getInstance(context!!)
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
        iv_closeFAQ.setOnClickListener { closeFAQ(this) }
    }

    private fun closeFAQ(fragment: Fragment) {
        val bottomActivity = (activity as BottomActivity)
        val fragmentManager = bottomActivity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        bottomActivity.tv_deckToOpen.visibility = View.VISIBLE
        bottomActivity.tv_wallet.visibility = View.VISIBLE
        bottomActivity.navigation.visibility = View.VISIBLE
        bottomActivity.fragmentLayout.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        closeFAQ(this)
        super.onDestroyView()
    }

}
