package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.LIST_OF_FRIENDS
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes.LOGIN
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.*
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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginAppManager private constructor(private var context: Context){

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.instance
    private var connectedToGoogle = false
    private lateinit var gso: GoogleSignInOptions
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var account: GoogleSignInAccount? = null
    private lateinit var googleBtnTextView: Button
    private val loginActivity = (context as LoginActivity)
    lateinit var connectedUser: User
    var gameInProgress = true
    var handlerTime = 1500L

    companion object : SingletonHolder<LoginAppManager, Context>(::LoginAppManager)

    //REGULAR CONNECTION AND SIGN IN

    fun regularConnection() {
        loginActivity.login_progressBar.visibility = View.VISIBLE
        val mail = loginActivity.etEmail.text.toString()
        val pass = loginActivity.etPassword.text.toString()
        val jsonBody = JsonObject()
        jsonBody.addProperty("user_email", mail)
        jsonBody.addProperty("user_password", pass)
        val connection = rickAndMortyAPI!!.connectUser(jsonBody)
        callRetrofit(connection, LOGIN)
    }

    fun regularSignIn() {
        val signInIntent = Intent(context, SignInActivity::class.java)
        context.startActivity(signInIntent)
    }

    // GOOGLE CONNECTION

    fun googleSetup() {
        loginActivity.sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        loginActivity.sign_in_button.setOnClickListener { googleSignIn() }

        gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(loginActivity, gso)
        googleBtnTextView = (loginActivity.sign_in_button.getChildAt(0) as Button)
    }

    private fun googleSignIn() {
        loginActivity.login_progressBar.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient?.signInIntent
        loginActivity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            connectedToGoogle = true
            Log.d(TAG, "handleGoogleSignInResult: connected")

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }

    }

    fun onGoogleConnectionResult(data: Intent?) {
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleGoogleSignInResult(task)
        val account = GoogleSignIn.getLastSignedInAccount(context)
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
            callRetrofit(connection, LOGIN)
        } else {
            Toast.makeText(context, "erreur", Toast.LENGTH_LONG).show()
        }
    }

    fun disconnectGoogleAccount() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener { task ->
            task.result
            Toast.makeText(context, context.getString(R.string.google_disconnected), Toast.LENGTH_SHORT).show()
            googleBtnTextView.text = context.getString(R.string.btn_connection_google)
            loginActivity.sign_in_button.setOnClickListener { googleSignIn() }
            connectedToGoogle = false
        }
    }

    // FACEBOOK CONNECTION

    fun facebookSetup() {
        loginActivity.FacebookCallbackManager = CallbackManager.Factory.create()
        loginActivity.facebook_login_button.setReadPermissions("email")

        loginActivity.facebook_login_button.registerCallback(loginActivity.FacebookCallbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                loginActivity.login_progressBar.visibility = View.VISIBLE
                val accessToken = loginResult.accessToken
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                Log.d(TAG, "isLoggedIn on success = $isLoggedIn")

                if (isLoggedIn) {
                    //"/100033490894253/picture"
                    val request = GraphRequest.newMeRequest(
                            accessToken
                    ) { obj , response ->
                        val result = response.jsonObject
                        try {

                            val userNameFB = result.getString("name")
                            val userEmail = result.getString("email")
                            val userId = result.getString("id")
                            //val userImage = obj.getString("picture") as JsonObject
                            //val jj = userImage.get("data")
                            val jsonBody = JsonObject()
                            //Log.d(TAG, "image : $userImage")
                            //Log.d(TAG, "data : $jj")
                            jsonBody.addProperty("user_email", userEmail)
                            jsonBody.addProperty("user_name", userNameFB)
                            jsonBody.addProperty("user_password", userId)
                            //jsonBody.addProperty("user_image", userImage)
                            jsonBody.addProperty("external_id", userId)
                            val connection = rickAndMortyAPI!!.connectUser(jsonBody)
                            callRetrofit(connection, LOGIN)
                        } catch (e: Throwable) {
                            Log.d(TAG, "error : $e")
                            e.printStackTrace()
                            Toast.makeText(context, "Facebook connection error", Toast.LENGTH_SHORT).show()
                            loginActivity.login_progressBar.visibility = View.GONE
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
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onError: " + exception.toString())
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Log.d(TAG, "isLoggedIn = $isLoggedIn")

        if (isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(loginActivity, Arrays.asList("public_profile"))
        }
    }


    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    when (type) {
                        LOGIN -> {
                            if (response.isSuccessful) {
                                val responseBody = response.body() as ResponseFromApi
                                val code = responseBody.code
                                if (code == 200) {
                                    loginActivity.login_progressBar.visibility = View.GONE
                                    if (connectedToGoogle) {
                                        googleBtnTextView.text = context.getString(R.string.btn_disconnection_google)
                                        loginActivity.sign_in_button.setOnClickListener { disconnectGoogleAccount() }
                                    }
                                    val results = responseBody.results?.userName
                                    Log.d(TAG, "body = ${response.body().toString()}")
                                    Toast.makeText(context, "code : $code, bienvenue $results", Toast.LENGTH_SHORT).show()
                                    val homeIntent = Intent(context, BottomActivity::class.java)
                                    connectedUser = responseBody.results!!
                                    //homeIntent.putExtra("user", responseBody.results)
                                    loginActivity.startActivity(homeIntent)
                                } else {
                                    loginActivity.login_progressBar.visibility = View.GONE
                                    if (loginActivity.etEmail.text.toString() == "" || loginActivity.etPassword.text.toString() == "") {
                                        Toast.makeText(context, context.getString(R.string.thanks_to_fill_all_fields), Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "login error code : $code, message ${responseBody.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                loginActivity.login_progressBar.visibility = View.GONE
                                Log.d(TAG, "error : ${response.errorBody()}")
                            }
                        }
                        LIST_OF_FRIENDS -> {

                        }
                        else -> Log.d(TAG, "error : ${response.errorBody()}")
                    }

                } else {
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: ${responseError.string()}")
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d(TAG, "fail : $t")
            }
        })

    }

}