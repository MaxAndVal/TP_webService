package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.SignInManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInManager: SignInManager
    private var loaderLiveData = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        loaderLiveData.observeOnce(Observer {
            progress_bar_sign_in.visibility = it
        })

        signInManager = SignInManager.getInstance(this)

        tv_alreadyAccount.setOnClickListener { finish() }

        btn_confSignIn.setOnClickListener {
            if (fieldsAreEmpties()) {
                Toast.makeText(this, getString(R.string.thanks_to_fill_all_fields), Toast.LENGTH_SHORT).show()
                progress_bar_sign_in.visibility = View.GONE
            } else {
                progress_bar_sign_in.visibility = View.VISIBLE
                val name = ed_username.text.toString()
                val email = ed_email.text.toString()
                val password = ed_password.text.toString()
                signInManager.signIn(name, email, password, loaderLiveData)
            }
        }

    }

    private fun fieldsAreEmpties(): Boolean {
        return ed_email.text.toString() == "" || ed_password.text.toString() == "" || ed_username.text.toString() == ""
    }

    override fun onBackPressed() {
        signInManager.cancelCall()
        super.onBackPressed()
    }

}
