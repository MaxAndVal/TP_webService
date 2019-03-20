package com.example.lpiem.rickandmortyapp.View.Settings

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Connection.TAG
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.SettingsManager
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

        if (!loginAppManager.connectedUser!!.externalId.isNullOrEmpty()) {
            Log.d(TAG, "ext ID = ${loginAppManager.connectedUser!!.externalId}")
            tv_change_password.isEnabled = false
            tv_change_password.isClickable = false
            tv_change_password.setTextColor(resources.getColor(R.color.grayDark, null))
        }

        tv_disconnect.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_NoActionBar)

            builder.setTitle(getString(R.string.deconnection_alert))
                    .setMessage(getString(R.string.deconnection_message))
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        loginAppManager.disconnectUser()
                        val handler = Handler()
                        handler.postDelayed({
                            settingsManager.disconnect.postValue(true)
                        }, 2000L)
                    }
                    .setNegativeButton(android.R.string.no) { dialog, which ->
                        // do nothing
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

        }
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

    override fun onDestroyView() {
        settingsManager.cancelCall()
        super.onDestroyView()
    }

}
