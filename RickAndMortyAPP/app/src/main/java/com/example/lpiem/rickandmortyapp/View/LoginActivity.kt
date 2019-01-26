package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyAPI
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

const val TAG = "TAG_M"
const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity(), Callback<ResponseFromApi> {

    private var callbackManager: CallbackManager? = null
    private lateinit var gso: GoogleSignInOptions
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var userNameFB: String? = null
    private var account: GoogleSignInAccount? = null
    private var displayIntent: Intent? = null
    private var rickAndMortyAPI: RickAndMortyAPI? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        displayIntent = Intent(this@LoginActivity, BottomActivity::class.java)
        rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance

        //REGULAR CONNECTION AND SIGN IN

        btnRegularConnection.setOnClickListener { regularConnection() }
        tv_signin.setOnClickListener { regularSignIn() }

        // GOOGLE

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener { signIn() }

        gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        // FACEBOOK

        callbackManager = CallbackManager.Factory.create()
        facebook_login_button.setReadPermissions("email")

        facebook_login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                login_progressBar.visibility = VISIBLE
                val token = loginResult.accessToken.token
                Log.d(TAG, "login onSuccess = ${loginResult.accessToken.userId}")
                Log.d(TAG, "onSuccess: token = $token")
                val accessToken = loginResult.accessToken
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                Log.d(TAG, "isLoggedIn on success = $isLoggedIn")
                var connection : Call<ResponseFromApi>?
                if (isLoggedIn) {
                    val request = GraphRequest.newMeRequest(
                            accessToken
                    ) { `object`, response ->
                        val result = response.jsonObject
                        try {
                            userNameFB = result.getString("name")
                            val userEmail = result.getString("email")
                            val userId = result.getString("id")
                            val userImage = result.getString("picture")
                            val jsonBody = JsonObject()
                            jsonBody.addProperty("user_email", userEmail)
                            jsonBody.addProperty("user_name", userNameFB)
                            jsonBody.addProperty("user_password", userId)
                            jsonBody.addProperty("user_image", userImage)
                            jsonBody.addProperty("external_id", userId)
                            Log.d(TAG, "just after graph request")
                            connection = rickAndMortyAPI!!.connectUser(jsonBody)
                            connection?.enqueue(this@LoginActivity)
                        } catch (e: Throwable) {
                            Log.d(TAG, "error : $e")
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,link,email,picture")
                    request.parameters = parameters
                    request.executeAsync()
                }
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
        Log.d(TAG, "isLoggedIn = $isLoggedIn")

        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
        }

    }

    private fun regularSignIn() {
        val signInIntent = Intent(this@LoginActivity, SignInActivity::class.java)
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
            Log.d(TAG, "child : ${(sign_in_button.getChildAt(0) as Button).text}" )
            (sign_in_button.getChildAt(0) as Button).text = "Se déconnecter"
            sign_in_button.setOnClickListener { disconnectGoogleAccount() }
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                val userName = account.displayName
                val userEmail = account.email
                val userId = account.id
                val userImage = account.photoUrl
                val jsonBody = JsonObject()
                jsonBody.addProperty("user_email", userEmail)
                jsonBody.addProperty("user_name", userName)
                jsonBody.addProperty("user_password", userId)
                jsonBody.addProperty("user_image", userImage.toString())
                jsonBody.addProperty("external_id", userId)
                val connection = rickAndMortyAPI!!.connectUser(jsonBody)
                connection.enqueue(this)
            } else {
                Toast.makeText(applicationContext, "erreur", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn() {
        login_progressBar.visibility = VISIBLE
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "handleSignInResult: connected")

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
            Toast.makeText(applicationContext, "Déconnecté de votre compte Google", Toast.LENGTH_SHORT).show()
            (sign_in_button.getChildAt(0) as Button).text = "Se connecter"
            sign_in_button.setOnClickListener { signIn() }
        }
    }

    private fun regularConnection() {

        login_progressBar.visibility = VISIBLE
        val mail = etEmail.text.toString()
        val pass = etPassword.text.toString()
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", mail)
        jsonBody.addProperty("user_password", pass)
        val connection = rickAndMortyAPI!!.connectUser(jsonBody)
        Log.d(TAG, "jsonBody : $jsonBody")
        Log.d(TAG, "$connection")
        connection.enqueue(this)
    }


    override fun onResponse(call: Call<ResponseFromApi>, response: Response<ResponseFromApi>) {
        if (response.isSuccessful) {
            val code = response.body()?.code
            if (response.body()?.code == 200) {
                login_progressBar.visibility = GONE
                val results = response.body()?.results?.userName
                Log.d(TAG, "body = ${response.body().toString()}")
                Toast.makeText(this, "code : $code, bienvenue $results", Toast.LENGTH_SHORT).show()
                displayIntent?.putExtra("user", response.body()?.results)
                startActivity(displayIntent)
            } else {
                login_progressBar.visibility = GONE
                if (etEmail.text.toString() == "" || etPassword.text.toString() == "") {
                    Toast.makeText(this, "Merci de remplir l'ensemble des champs pour vous connecter", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "code : $code, message ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            login_progressBar.visibility = GONE
            Log.d(TAG, "error : ${response.errorBody()}")
        }
    }


    override fun onFailure(call: Call<ResponseFromApi>, t: Throwable) {
        Log.d(TAG, "failure : $t")
    }


    override fun onBackPressed() {
        finish()
    }

}

