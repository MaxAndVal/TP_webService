package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.settings.ChangePasswordManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.R.color.*
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.fragment_change_password.*

class PasswordFragment : androidx.fragment.app.Fragment() {

    private lateinit var changePasswordManager: ChangePasswordManager
    private lateinit var changePasswordObserver: Observer<Boolean>

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordManager = ChangePasswordManager.getInstance(context!!)
        changePasswordObserver = Observer { isChangeIsSuccesfull ->
            if (isChangeIsSuccesfull) closeChangePassword(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_closeFragment.setOnClickListener { closeChangePassword(this) }
        btn_changePassword.setOnClickListener { changePassword() }

    }

    private fun changePassword() {
        val oldPass = ed_OldPassword.text.toString()
        val newPass = ed_NewPassword.text.toString()
        val newPassConf = ed_NewPasswordConf.text.toString()
        var isEmpty = false

        if (oldPass.isEmpty()) {
            isEmpty = true
            textInputOldPassword.setHelperTextColor(resources.getColorStateList(R.color.ErrorLightRed))
        }
        if (newPass.isEmpty()) {
            isEmpty = true
            textInputNewPassword.setHelperTextColor(resources.getColorStateList(R.color.ErrorLightRed))
        }
        if (newPassConf.isEmpty()) {
            isEmpty = true
            textInputNewPasswordConf.setHelperTextColor(resources.getColorStateList(R.color.ErrorLightRed))
        }

        if (!isEmpty) {
            if (newPass.equals(newPassConf)) {
                changePasswordManager.isPasswordChangeSucceded.observeOnce(changePasswordObserver)
                changePasswordManager.changePassword(oldPass, newPass)
                closeChangePassword(this)
            } else {
                tv_errorInput.text = getString(R.string.ErrorSamePassword)
                Toast.makeText(context, "new passwords are not the same", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun closeChangePassword(fragment: Fragment) {
        val bottomActivity = (activity as BottomActivity)
        val fragmentManager = bottomActivity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        bottomActivity.tv_deckToOpen.visibility = View.VISIBLE
        bottomActivity.tv_wallet.visibility = View.VISIBLE
        bottomActivity.navigation.visibility = View.VISIBLE
        bottomActivity.fragmentLayout.visibility = View.VISIBLE
        bottomActivity.tv_message.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        closeChangePassword(this)
        super.onDestroyView()
    }
}
