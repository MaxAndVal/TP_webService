package com.example.lpiem.rickandmortyapp.Manager.settings

import android.content.Context
import android.util.Log
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
import com.example.lpiem.rickandmortyapp.View.Settings.PasswordFragment
import com.example.lpiem.rickandmortyapp.View.TAG

class SettingsManager internal constructor(private val context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)

    var faqManager: FaqManager? = null
    var listOfFaqFromSM: List<FAQ>? = null
    private var faqLiveData = MutableLiveData<ListOfFAQ>()
    var openFaqLiveData = MutableLiveData<Fragment>()
    var openFragChangePassLiveData = MutableLiveData<PasswordFragment>()

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    private fun getFAQTreatment(response: ListOfFAQ) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            listOfFaqFromSM = response.FAQs
            Log.d(TAG, listOfFaqFromSM.toString() + "SM")
            if (listOfFaqFromSM != null) {
                faqManager = FaqManager.getInstance(context)
                faqManager?.faqLoaderLiveData?.postValue(listOfFaqFromSM)
            } else {
                Log.d(TAG, "listOfFAQ is null")
            }
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
        }
    }

    fun openFragmentFAQ(fragment: Fragment) {
        openFaqLiveData.postValue(fragment)

        faqLiveData = rickAndMortyAPI.getFAQ()
        faqLiveData.observeOnce(Observer {
            getFAQTreatment(it)
        })

    }

    fun openFragmentChangePassword(passwordFragment: PasswordFragment) {
        openFragChangePassLiveData.postValue(passwordFragment)
    }
}
