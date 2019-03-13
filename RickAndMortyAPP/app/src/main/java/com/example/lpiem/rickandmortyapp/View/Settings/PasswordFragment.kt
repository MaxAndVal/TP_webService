package com.example.lpiem.rickandmortyapp.View.Settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.settings.ChangePasswordManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.fragment_password.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PasswordFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PasswordFragment : androidx.fragment.app.Fragment() {

    private lateinit var changePasswordManager: ChangePasswordManager
    private lateinit var changePasswordObserver : Observer<Boolean>

    companion object : SingletonHolder<SettingsManager, Context>(::SettingsManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       changePasswordManager = ChangePasswordManager.getInstance(context!!)
        changePasswordObserver = Observer {isChangeIsSuccesfull ->
                if(isChangeIsSuccesfull)closeChangePassword(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_closeFragment.setOnClickListener { closeChangePassword(this) }
        btn_changePassword.setOnClickListener{changePassword()}

    }

    private fun changePassword() {
        val oldPass = ed_OldPassword.text.toString()
        val newPass = ed_NewPassword.text.toString()
        val newPassConf = ed_NewPasswordConf.text.toString()

    if(!newPass.isEmpty() && !oldPass.isEmpty() && !newPassConf.isEmpty()){
        if (newPass.equals(newPassConf)){
            changePasswordManager.isPasswordChangeSucceded.observeOnce(changePasswordObserver)
            changePasswordManager.changePassword(oldPass, newPass)
            closeChangePassword(this)
        }else{
            Toast.makeText(context, "new passwords are not the same", Toast.LENGTH_LONG).show()
        }
    }else{
        Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
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
