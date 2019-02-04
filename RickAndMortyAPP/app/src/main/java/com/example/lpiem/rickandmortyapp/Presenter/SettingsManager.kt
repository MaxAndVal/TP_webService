package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.*
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsAdapter
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.TAG
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsManager private constructor(private val context: Context) : SettingsOnClickInterface{
    override fun todo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    var settingsFragment: SettingsFragment? = null
    internal lateinit var recyclerView: RecyclerView

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    fun captureFragmentInstance(fragment: SettingsFragment) {
        settingsFragment = fragment
    }

    fun captureRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d(TAG, response.toString())
                    when (type) {
                        RetrofitCallTypes.GET_FAQ -> {
                            var settings = response.body() as ListOfFriends
                            var code = settings.code
                            if (code == 200) {
                                settingsFragment?.listOfFAQ = response.body() as ListOfFAQ
                                if (settingsFragment?.listOfFAQ != null) {
                                    Log.d(TAG, "List : " + settingsFragment?.listOfFAQ)
                                    recyclerView.adapter = SettingsAdapter(settingsFragment?.listOfFAQ!!, this@SettingsManager)
                                    recyclerView.adapter?.notifyDataSetChanged()
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

    fun fragmentFAQ() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}