package com.example.lpiem.rickandmortyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
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
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

const val TAG = "TAG_M"
const val RC_SIGN_IN = 1


class MainActivity : AppCompatActivity(), Callback<ResponseFromApi> {


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
    private var rickAndMortyAPI: RickAndMortyAPI? = null
    private lateinit var btnSignin: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayIntent = Intent(this@MainActivity, DisplayActivity::class.java)

        etEmail = findViewById(R.id.etEmail)
        etPassword  =findViewById(R.id.etPassword)
        btnRegularConnection = findViewById(R.id.btnRegularConnection)
        btnSignin = findViewById(R.id.tv_signin)

        btnRegularConnection.setOnClickListener { regularConnection() }
        btnSignin.setOnClickListener { regularSignIn() }

        userNameTV = findViewById(R.id.userNameTV)
        userNameTV.visibility = View.INVISIBLE
        //disconnectGoogleBtn = findViewById(R.id.disconnectGoogle)
        //disconnectGoogleBtn.visibility = View.INVISIBLE
        //disconnectGoogleBtn.setOnClickListener { disconnectGoogleAccount() }


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

    private fun regularSignIn() {
       var signInIntent = Intent(this@MainActivity, signin_activity::class.java)
        startActivity(signInIntent)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

        val mail = etEmail.text.toString()
        val pass = etPassword.text.toString()
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", mail)
        jsonBody.addProperty("user_password", pass)
        var connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        connection.enqueue(this)
    }

    override fun onResponse(call: Call<ResponseFromApi>, response: Response<ResponseFromApi>) {
        if (response.isSuccessful) {
            var code = response.body()?.code
            var results = response.body()?.results?.userName
            Log.d(TAG, "body = ${response.body()}")
            Toast.makeText(this, "code : $code, bienvenue $results", Toast.LENGTH_SHORT).show()
            startActivity(displayIntent)
        } else {
            Log.d(TAG, "error : ${response.errorBody()}")
        }
    }


    override fun onFailure(call: Call<ResponseFromApi>, t: Throwable) {
        Log.d(TAG, "failure : $t")
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    override fun onResume() {
        super.onResume()

    }
}

