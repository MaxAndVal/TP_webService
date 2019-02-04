package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.R
import com.facebook.CallbackManager
import com.google.android.gms.common.SignInButton
import kotlinx.android.synthetic.main.activity_login.*

const val TAG = "TAG_M"
const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    internal var FacebookCallbackManager: CallbackManager? = null
    private lateinit var loginAppManager: LoginAppManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loginAppManager = LoginAppManager.getInstance(this)

        regularConnectionSetup()
        setUpGoogle()
        loginAppManager.facebookSetup()
    }


    private fun regularConnectionSetup() {
        login_progressBar.visibility = View.VISIBLE
        val mail = etEmail.text.toString()
        val pass = etPassword.text.toString()
        btnRegularConnection.setOnClickListener { loginAppManager.regularConnection(mail, pass) }
        tv_signIn.setOnClickListener { loginAppManager.regularSignIn() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Facebook
        FacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        //Google
        if (requestCode == RC_SIGN_IN) {
            loginAppManager.onGoogleConnectionResult(data)
        }
    }


    private fun setUpGoogle() {
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        loginAppManager.googleSetup()
    }


    override fun onResume() {
        super.onResume()
        clearEditTexts()
    }

    private fun clearEditTexts() {
        etEmail.text.clear()
        etPassword.text.clear()
    }

    override fun onBackPressed() {
        finish()
    }

}

