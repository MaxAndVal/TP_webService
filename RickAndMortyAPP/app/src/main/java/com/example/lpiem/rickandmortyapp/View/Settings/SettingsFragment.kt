package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingsFragment : androidx.fragment.app.Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var settingsManager: SettingsManager
    private lateinit var loginAppManager: LoginAppManager

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        settingsManager = SettingsManager.getInstance(context!!)
        loginAppManager = LoginAppManager.getInstance(context!!)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_faq.setOnClickListener { settingsManager.openFragmentFAQ(FAQ_Fragment()) }

        displayUserInformation()
    }

    private fun displayUserInformation() {
        val user = loginAppManager.connectedUser!!
        tv_email.text = user.userEmail
        tv_name.text = user.userName
        //Picasso.get().load(R.drawable.workshop).fit().centerCrop().into(iv_profile_back)
        Picasso.get().load(user.userImage).placeholder(R.drawable.ic_person_black_24dp).into(iv_profile_picture)
    }

}
