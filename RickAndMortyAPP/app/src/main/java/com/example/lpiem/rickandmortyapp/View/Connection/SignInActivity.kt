package com.example.lpiem.rickandmortyapp.View.Connection

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.SignInManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInManager: SignInManager
    private lateinit var loaderObserver: Observer<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signInManager = SignInManager.getInstance(this)

        loaderObserver = Observer {
            progress_bar_sign_in.visibility = it
        }

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
                signInManager.signIn(name, email, password)
                // close keyboard on validation
                closeKeyboard()
            }
        }

    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        signInManager.loaderLiveData.observeForever(loaderObserver)
    }

    private fun fieldsAreEmpties(): Boolean {
        return ed_email.text.toString() == "" || ed_password.text.toString() == "" || ed_username.text.toString() == ""
    }

    override fun onBackPressed() {
        signInManager.cancelCall()
        signInManager.loaderLiveData.removeObserver(loaderObserver)
        super.onBackPressed()
    }

}
