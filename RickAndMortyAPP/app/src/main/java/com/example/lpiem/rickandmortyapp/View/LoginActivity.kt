package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.R
import com.facebook.CallbackManager
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
        loginAppManager.googleSetup()
        loginAppManager.facebookSetup()
    }


    private fun regularConnectionSetup() {
        btnRegularConnection.setOnClickListener { loginAppManager.regularConnection() }
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

