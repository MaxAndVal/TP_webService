package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.Presenter.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.fragment_settings.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingsFragment : androidx.fragment.app.Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var settingsManager: SettingsManager
    private var rickAndMortyAPI: RickAndMortyAPI?=null
    private lateinit var loginAppManager: LoginAppManager
    internal var user: User? = null

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        settingsManager = SettingsManager.getInstance(context!!)
        loginAppManager = LoginAppManager.getInstance(context!!)
        user = loginAppManager.connectedUser

        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        settingsManager = SettingsManager.getInstance(context!!)
        if (settingsManager.settingsFragment == null) {
            settingsManager.captureFragmentInstance(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_faq.setOnClickListener { settingsManager.openFragmentFAQ(FAQ_Fragment()) }

    }

}
