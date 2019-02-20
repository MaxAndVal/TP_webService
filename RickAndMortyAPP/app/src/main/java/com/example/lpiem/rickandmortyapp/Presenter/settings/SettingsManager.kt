package com.example.lpiem.rickandmortyapp.Presenter.settings

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.FAQ
import com.example.lpiem.rickandmortyapp.Model.ListOfFAQ
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.Settings.FAQAdapter
import com.example.lpiem.rickandmortyapp.View.Settings.FAQ_Fragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsOnClickInterface
import com.example.lpiem.rickandmortyapp.View.TAG
import kotlinx.android.synthetic.main.activity_bottom.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsManager internal constructor(private val context: Context) : SettingsOnClickInterface {
    override fun todo(item: FAQAdapter.ViewHolder) {
        if (item.faqResponse.visibility == View.GONE) {
            item.faqResponse.visibility = View.VISIBLE;
        } else {
            item.faqResponse.visibility = View.GONE;
        }
    }

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context).instance
    var settingsFragment: SettingsFragment? = null
    var faqFragment: FAQ_Fragment? = null
    var faqManager: FaqManager? = null
    var listOfFAQfromSM: List<FAQ>? = null

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    fun captureFragmentInstance(fragment: SettingsFragment) {
        settingsFragment = fragment
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    val result = response.body()
                    when (type) {
                        RetrofitCallTypes.GET_FAQ -> {
                            getFAQTreatment(result as ListOfFAQ)
                        }
                    }
                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

    private fun getFAQTreatment(response: ListOfFAQ) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            listOfFAQfromSM = response.FAQs
            Log.d(TAG, listOfFAQfromSM.toString() + "SM")
            if (listOfFAQfromSM != null) {
                faqManager = FaqManager.getInstance(context)
                faqManager!!.recyclerView.adapter = FAQAdapter(listOfFAQfromSM!!, this@SettingsManager)
                faqManager!!.recyclerView.adapter?.notifyDataSetChanged()
            } else {
                Log.d(TAG, "listOfFAQ is null")
            }
        } else {
            Toast.makeText(context, "code : $code, message $message", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFragmentFAQ(fragment: Fragment) {
        val resultCall = rickAndMortyAPI!!.getFAQ()
        callRetrofit(resultCall, RetrofitCallTypes.GET_FAQ)
        val fragmentManager = (context as BottomActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flMain, fragment).addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        context.flMain.bringToFront()
        context.tv_deckToOpen.visibility = View.GONE
        context.tv_wallet.visibility = View.GONE
        context.navigation.visibility = View.GONE
        context.fragmentLayout.visibility = View.GONE

    }
}
