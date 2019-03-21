package com.example.lpiem.rickandmortyapp.View.Connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

const val TAG = "TAG_M"
const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    var facebookCallbackManager: CallbackManager? = null
    private lateinit var loginAppManager: LoginAppManager
    private var doubleBackToExitPressedOnce = false
    private lateinit var loaderObserver: Observer<Int>
    private lateinit var googleBtnSwitchObserver : Observer<Boolean>
    private lateinit var resolveIntentObserver: Observer<Intent>
    private var googleBtnLabel: Button? = null
    private lateinit var facebookInitObserver: Observer<Unit>
    private lateinit var alreadyConnectedWithFacebookObserver: Observer<Boolean>
    private lateinit var finishLoginActivityObserver: Observer<Boolean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginAppManager = LoginAppManager.getInstance(this)
        loginAppManager.mGoogleSignInClient = GoogleSignIn.getClient(this, loginAppManager.instanciateGSO())

        initObservers()
        triggerLivesData()

        regularConnectionSetup()
        setUpGoogle()
        loginAppManager.facebookSetup()

        googleBtnLabel = (sign_in_button.getChildAt(0) as Button)
    }


    private fun regularConnectionSetup() {
        btnRegularConnection.setOnClickListener {
            loginAppManager.regularConnection(etEmail.text.toString(), etPassword.text.toString())
            // Check if no view has focus before hiding the keyboard:
            closeKeyboard()
        }
        tv_signIn.setOnClickListener { loginAppManager.regularSignIn() }
        tv_lostPassword.setOnClickListener { loginAppManager.openActivityLostPassword() }

    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Facebook
        facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
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

    private fun googleSignIn() {
        loginAppManager.loaderDisplay.postValue(View.VISIBLE)
        val signInIntent = loginAppManager.mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onResume() {
        super.onResume()
        clearEditTexts()
    }

    private fun clearEditTexts() {
        etEmail.text?.clear()
        etPassword.text?.clear()
    }

    private fun initObservers() {

        finishLoginActivityObserver = Observer {
            if (it && loginAppManager.connectedUser!=null){
                finish()
            }

        }

        loaderObserver = Observer { isVisible ->
            login_progressBar.visibility = isVisible
        }

        googleBtnSwitchObserver = Observer {connect ->
            if (connect) {
                googleBtnLabel?.text = getString(R.string.btn_connection_google)
                sign_in_button.setOnClickListener {
                    googleSignIn()
                }
            } else {
                googleBtnLabel?.text = getString(R.string.btn_disconnection_google)
                sign_in_button.setOnClickListener {
                    loginAppManager.disconnectGoogleAccount(true)
                }
            }
        }

        resolveIntentObserver = Observer { intent ->
            startActivity(intent)
        }

        facebookInitObserver = Observer {
            facebookCallbackManager = CallbackManager.Factory.create()
            facebook_login_button.setReadPermissions("email")
            facebook_login_button.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {
                    loginAppManager.facebookSuccess(loginResult)
                }

                override fun onCancel() {
                    loginAppManager.facebookCancel()
                }

                override fun onError(exception: FacebookException) {
                    loginAppManager.facebookError(exception)
                }
            })
        }

        alreadyConnectedWithFacebookObserver = Observer {isLoggedIn ->
            if (isLoggedIn) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday, user_friends"))
            }
        }
    }

    private fun triggerLivesData() {
        loginAppManager.finishActivityLiveData.observeOnce(finishLoginActivityObserver)
        loginAppManager.alreadyConnectedToFacebook.observeForever(alreadyConnectedWithFacebookObserver)
        loginAppManager.facebookInit.observeForever(facebookInitObserver)
        loginAppManager.resolveIntent.observeForever(resolveIntentObserver)
        loginAppManager.googleBtnSwitch.observeForever(googleBtnSwitchObserver)
        loginAppManager.loaderDisplay.observeForever(loaderObserver)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            loginAppManager.loaderDisplay.removeObserver(loaderObserver)
            loginAppManager.alreadyConnectedToFacebook.removeObserver(alreadyConnectedWithFacebookObserver)
            loginAppManager.facebookInit.removeObserver(facebookInitObserver)
            loginAppManager.resolveIntent.removeObserver(resolveIntentObserver)
            loginAppManager.googleBtnSwitch.removeObserver(googleBtnSwitchObserver)
            loginAppManager.cancelCall()
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.double_back_to_quit_app), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}

