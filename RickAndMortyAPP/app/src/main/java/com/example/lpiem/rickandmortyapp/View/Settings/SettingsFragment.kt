package com.example.lpiem.rickandmortyapp.View.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : androidx.fragment.app.Fragment() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var loginAppManager: LoginAppManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        tv_change_password.setOnClickListener { settingsManager.openFragmentChangePassword(PasswordFragment()) }
        displayUserInformation()
    }

    private fun displayUserInformation() {
        val user = loginAppManager.connectedUser!!
        tv_email.text = user.userEmail
        tv_name.text = user.userName
        Picasso.get().load(user.userImage).placeholder(R.drawable.ic_person_black_24dp).into(iv_profile_picture)
    }

}
