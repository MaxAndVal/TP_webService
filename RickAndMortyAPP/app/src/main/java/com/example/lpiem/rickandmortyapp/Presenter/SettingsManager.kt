package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.*
import com.example.lpiem.rickandmortyapp.Presenter.collection.FaqManager
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

class SettingsManager internal constructor(private val context: Context) : SettingsOnClickInterface{
    override fun todo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    var settingsFragment: SettingsFragment? = null
    var faqFragment : FAQ_Fragment? = null
    var faqManager : FaqManager?=null
    var listOfFAQfromSM: List<FAQ>?=null
//    internal lateinit var recyclerView: RecyclerView

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    fun captureFragmentInstance(fragment: SettingsFragment) {
        settingsFragment = fragment
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.GET_FAQ -> {
                            var settings = response.body() as ListOfFAQ
                            var code = settings.code
                            if (code == 200) {
                                listOfFAQfromSM = settings.FAQs
                                Log.d(TAG, listOfFAQfromSM.toString()+"SM")
                                if (listOfFAQfromSM != null) {
                                    FaqManager.getInstance(context!!).recyclerView.adapter = FAQAdapter(listOfFAQfromSM!!, this@SettingsManager)
                                    FaqManager.getInstance(context!!).recyclerView.adapter?.notifyDataSetChanged()
                                }else{
                                    Log.d(TAG, "listOfFAQ is null")
                                }
                            } else {
                                Toast.makeText(context, "code : $code, message ${settings.message}", Toast.LENGTH_SHORT).show()
                            }
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

    fun fragmentFAQ(fragment : Fragment) {

        val resultCall = rickAndMortyAPI!!.getFAQ()
        callRetrofit(resultCall, RetrofitCallTypes.GET_FAQ)

        var FAQFragment = (context as BottomActivity).supportFragmentManager
        val fragmentTransaction = FAQFragment.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }
}
