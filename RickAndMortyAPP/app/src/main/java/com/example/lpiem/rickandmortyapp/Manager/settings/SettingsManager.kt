package com.example.lpiem.rickandmortyapp.Manager.settings

import android.content.Context
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.FAQ
import com.example.lpiem.rickandmortyapp.Model.ListOfFAQ
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Settings.FAQAdapter
import com.example.lpiem.rickandmortyapp.View.Settings.PasswordFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.fragment_change_password.*

class SettingsManager internal constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)

    var faqManager: FaqManager? = null
    var listOfFAQfromSM: List<FAQ>? = null
    private var FAQLiveData = MutableLiveData<ListOfFAQ>()

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    private fun getFAQTreatment(response: ListOfFAQ) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            listOfFAQfromSM = response.FAQs
            Log.d(TAG, listOfFAQfromSM.toString() + "SM")
            if (listOfFAQfromSM != null) {
                faqManager = FaqManager.getInstance(context)
                faqManager!!.recyclerView.adapter = FAQAdapter(listOfFAQfromSM!!)
                faqManager!!.recyclerView.adapter?.notifyDataSetChanged()
            } else {
                Log.d(TAG, "listOfFAQ is null")
            }
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    fun openFragmentFAQ(fragment: Fragment) {

        val fragmentManager = (context as BottomActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flMain, fragment).addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        //TODO : pass to Activity method
        context.flMain.bringToFront()
        context.tv_deckToOpen.visibility = GONE
        context.tv_wallet.visibility = GONE
        context.navigation.visibility = GONE
        context.fragmentLayout.visibility = GONE

        FAQLiveData = rickAndMortyAPI.getFAQ()
        FAQLiveData.observeOnce(Observer {
            getFAQTreatment(it)
        })

    }

    fun openFragmentChangePassword(passwordFragment: PasswordFragment) {
        val fragmentManager = (context as BottomActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flMain, passwordFragment).addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        //TODO : pass to Activity method
        context.flMain.bringToFront()
        context.tv_deckToOpen.visibility = GONE
        context.tv_wallet.visibility = GONE
        context.navigation.visibility = GONE
        context.fragmentLayout.visibility = GONE
        context.tv_message.visibility = GONE

    }
}
