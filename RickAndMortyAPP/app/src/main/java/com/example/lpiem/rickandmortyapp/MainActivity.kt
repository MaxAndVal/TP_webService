package com.example.lpiem.rickandmortyapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import org.json.JSONException

import java.util.Arrays

const val TAG = "TAG_M"
const val RC_SIGN_IN = 1


class MainActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private lateinit var facebookLoginButton: LoginButton
    private var token: String? = null
    private lateinit var gso: GoogleSignInOptions
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var signInButton: SignInButton
    private lateinit var userNameTV: TextView
    private lateinit var disconnectGoogleBtn: Button
    private var userNameGG: String? = null
    private var userNameFB: String? = null
    private var account: GoogleSignInAccount? = null
    private var displayIntent: Intent? = null
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegularConnection: Button
    private lateinit var etLogin: EditText
    private var rickAndMortyAPI: RickAndMortyAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayIntent = Intent(this@MainActivity, DisplayActivity::class.java)

        etEmail = findViewById(R.id.etEmail)
        etPassword  =findViewById(R.id.etPassword)
        btnRegularConnection = findViewById(R.id.btnRegularConnection)
        etLogin = findViewById(R.id.etLogin)

        btnRegularConnection.setOnClickListener { regularConnection() }

        userNameTV = findViewById(R.id.userNameTV)
        userNameTV.visibility = View.INVISIBLE
        disconnectGoogleBtn = findViewById(R.id.disconnectGoogle)
        disconnectGoogleBtn.visibility = View.INVISIBLE
        disconnectGoogleBtn.setOnClickListener { disconnectGoogleAccount() }


        facebookLoginButton = findViewById(R.id.login_button)
        callbackManager = CallbackManager.Factory.create()

        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)


        // FACEBOOK

        facebookLoginButton.setReadPermissions("email")

        Log.d(TAG, "onCreate: callBackManager = $callbackManager")
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                token = loginResult.accessToken.token
                Log.d(TAG, "onSuccess: token = $token")



            }

            /*
            * this.loginButton!!.registerCallback(this.callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Toast.makeText(this@MainActivity.applicationContext, "Bienvenue " + this@MainActivity.userNameFB!!, Toast.LENGTH_SHORT).show()
                this@MainActivity.startActivity(this@MainActivity.dispalyIntent)
            }


            override fun onCancel() {
                Log.d(TAG, "onCancel: ")
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@MainActivity.applicationContext, exception.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onError: " + exception.toString())
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = false

        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            val request = GraphRequest.newMeRequest(
                    accessToken
            ) { `object`, response ->
                val reponse = response.jsonObject//new JSONObject(response.toString());
                try {
                    this@MainActivity.userNameFB = reponse.getString("name")
                    //logInWith="facebook";
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,link")
            request.parameters = parameters
            request.executeAsync()
        }
            * */

            override fun onCancel() {
                Log.d(TAG, "onCancel: ")
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(applicationContext, exception.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onError: " + exception.toString())
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            val request = GraphRequest.newMeRequest(
                    accessToken
            ) { `object`, response ->
                val result = response.jsonObject
                try {
                    userNameFB = result.getString("name")
                    userNameTV.text = userNameFB
                    userNameTV.visibility = View.VISIBLE
                    Log.d(TAG, "onCompleted: name = $userNameFB")
                    Toast.makeText(applicationContext, "Bienvenue $userNameFB", Toast.LENGTH_SHORT).show()
                    LoginManager.getInstance().unregisterCallback(callbackManager)
                    startActivity(displayIntent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,link")
            request.parameters = parameters
            request.executeAsync()
        }




        // GOOGLE
        gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setOnClickListener { signIn() }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            signInButton.visibility = View.INVISIBLE
            disconnectGoogleBtn.visibility = View.VISIBLE
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                userNameGG = account.displayName
                userNameTV.text = userNameGG
                userNameTV.visibility = View.VISIBLE
            } else {
                userNameTV.visibility = View.INVISIBLE
            }
            startActivity(displayIntent)
        }


    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        this.callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            this.handleSignInResult(task)
            this.signInButton!!.visibility = View.INVISIBLE
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                this.userNameGG = account.displayName
                this.userNameTV!!.text = this.userNameGG
                this.userNameTV.visibility = View.VISIBLE
            } else {
            }
            this.startActivity(this.dispalyIntent)
        }
    }*/

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        if (account != null) {
            userNameGG = account!!.displayName
            userNameTV.text = userNameGG
            userNameTV.visibility = View.VISIBLE
        } else {
            userNameTV.visibility = View.INVISIBLE
        }
    }

    /*override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)


        if (account != null) {
            this.userNameGG = account.displayName
            this.userNameTV!!.text = this.userNameGG
        } else {
        }


    }*/

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "handleSignInResult: connected")
            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }

    }

    private fun disconnectGoogleAccount() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener { task ->
            task.result
            Log.d(TAG, "onComplete: disconnectGoogle")
            //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (account != null) {
                userNameGG = account!!.displayName
                userNameTV.text = userNameGG
                userNameTV.visibility = View.VISIBLE
                userNameTV.setText(R.string.empty)
            } else {
                userNameTV.visibility = View.INVISIBLE

            }
        }
        signInButton.visibility = View.VISIBLE
        disconnectGoogleBtn.visibility = View.INVISIBLE
    }


    private fun regularConnection() {

        val name = etLogin.text.toString()
        val mail = etEmail.text.toString()
        val pass = etPassword.text.toString()
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        rickAndMortyAPI!!.createUser(name, mail, pass)
        //TODO : to finish

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    override fun onResume() {
        super.onResume()

    }
}

