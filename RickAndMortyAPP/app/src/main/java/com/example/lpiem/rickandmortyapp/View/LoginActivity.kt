package com.example.lpiem.rickandmortyapp.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginAppManager = LoginAppManager.getInstance(this)

        regularConnectionSetup()
        setUpGoogle()
        loginAppManager.facebookSetup()
    }


    private fun regularConnectionSetup() {
        btnRegularConnection.setOnClickListener {
            loginAppManager.regularConnection(etEmail.text.toString(), etPassword.text.toString())
            login_progressBar.visibility = View.VISIBLE
            // Check if no view has focus before hiding the keyboard:
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as
                        InputMethodManager
                imm.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
        }
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
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "S'il vous plaît, cliquez sur retour une seconde fois pour quitter", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}

