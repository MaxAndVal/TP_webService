package com.example.lpiem.rickandmortyapp.Manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Data.JsonProperty
import com.example.lpiem.rickandmortyapp.Data.JsonProperty.*
import com.example.lpiem.rickandmortyapp.Data.RickAndMortyRetrofitSingleton
import com.example.lpiem.rickandmortyapp.Data.SUCCESS
import com.example.lpiem.rickandmortyapp.Model.ResponseFromApi
import com.example.lpiem.rickandmortyapp.Model.User
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import com.example.lpiem.rickandmortyapp.View.BottomActivity
import com.example.lpiem.rickandmortyapp.View.LoginActivity
import com.example.lpiem.rickandmortyapp.View.SignInActivity
import com.example.lpiem.rickandmortyapp.View.TAG
import com.facebook.AccessToken
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject

class LoginAppManager private constructor(private var context: Context) {

    private val rickAndMortyAPI = RickAndMortyRetrofitSingleton.getInstance(context)
    private var connectedToGoogle = false
    private lateinit var gso: GoogleSignInOptions
    var mGoogleSignInClient: GoogleSignInClient? = null
    private var account: GoogleSignInAccount? = null
    var connectedUser: User? = null
    var gameInProgress = true
    var memoryInProgress = true
    private var loginLiveData = MutableLiveData<ResponseFromApi>()
    lateinit var loaderDisplay: MutableLiveData<Int>
    var googleBtnSwitch = MutableLiveData<Boolean>()
    var resolveIntent = MutableLiveData<Intent>()
    var facebookInit = MutableLiveData<Unit>()
    var alreadyConnectedToFacebook = MutableLiveData<Boolean>()

    companion object : SingletonHolder<LoginAppManager, Context>(::LoginAppManager)

    //REGULAR CONNECTION AND SIGN IN

    fun regularConnection(mail: String, pass: String) {
        if (mail == "" || pass == "") {
            Toast.makeText(context, context.getString(R.string.thanks_to_fill_all_fields), Toast.LENGTH_SHORT).show()
        } else {
            loaderDisplay.postValue(View.VISIBLE)
            val jsonBody = JsonObject()
            jsonBody.addProperty(JsonProperty.UserEmail.string, mail)
            jsonBody.addProperty(JsonProperty.UserPassword.string, pass)
            loginLiveData = rickAndMortyAPI.login(jsonBody)
            loginLiveData.observeOnce(Observer {
                loginTreatment(it)
            })
        }
    }

    fun regularSignIn() {
        val signInIntent = Intent(context, SignInActivity::class.java)
        resolveIntent.postValue(signInIntent)
    }

    // GOOGLE CONNECTION

    fun googleSetup() {

        googleBtnSwitch.postValue(true)

        gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient((context as LoginActivity), gso)
        /////googleBtnTextView = ((context as LoginActivity).sign_in_button.getChildAt(0) as Button)
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
            loaderDisplay.postValue(View.GONE)
            Toast.makeText(context, "Une erreur est arrivée lors de la connection, merci de réessayer plus tard.", Toast.LENGTH_SHORT).show()
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
            jsonBody.addProperty(UserEmail.string, userEmail)
            jsonBody.addProperty(UserName.string, userName)
            jsonBody.addProperty(UserPassword.string, userId)
            jsonBody.addProperty(UserImage.string, userImage.toString())
            jsonBody.addProperty(ExternalID.string, userId)

            loginLiveData = rickAndMortyAPI.login(jsonBody)
            loginLiveData.observeOnce(Observer {
                loginTreatment(it)
            })

        } else {
            Toast.makeText(context, "erreur : ${data.toString()}", Toast.LENGTH_SHORT).show()
            loaderDisplay.postValue(View.GONE)
        }
    }

    fun disconnectGoogleAccount(verbose: Boolean) {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener { task ->
            task.result
            if (verbose) Toast.makeText(context, context.getString(R.string.google_disconnected), Toast.LENGTH_SHORT).show()
            googleBtnSwitch.postValue(true)
            googleBtnSwitch.postValue(true)
            connectedToGoogle = false
        }
    }

    // FACEBOOK CONNECTION

    fun facebookSetup() {
        facebookInit.postValue(Unit)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Log.d(TAG, "isLoggedIn = $isLoggedIn")
        alreadyConnectedToFacebook.postValue(isLoggedIn)

    }

    fun facebookSuccess(loginResult: LoginResult) {
        loaderDisplay.postValue(View.VISIBLE)
        val accessToken = loginResult.accessToken
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Log.d(TAG, "isLoggedIn on success = $isLoggedIn")

        if (isLoggedIn) {
            val request = GraphRequest.newMeRequest(
                    accessToken
            ) { obj, response ->
                val result = response.jsonObject
                Log.d(TAG, "request Body: $result")
                try {
                    val userNameFB = result.getString("name")
                    val userEmail = result.getString("email")
                    val userId = result.getString("id")
                    val image = "https://graph.facebook.com/$userId/picture?type=large"
                    val jsonBody = JsonObject()

                    jsonBody.addProperty(UserEmail.string, userEmail)
                    jsonBody.addProperty(UserName.string, userNameFB)
                    jsonBody.addProperty(UserPassword.string, userId)
                    jsonBody.addProperty(UserImage.string, image)
                    jsonBody.addProperty(ExternalID.string, userId)

                    loginLiveData = rickAndMortyAPI.login(jsonBody)
                    loginLiveData.observeOnce(Observer {
                        loginTreatment(it)
                    })

                } catch (e: Throwable) {
                    Log.d(TAG, "error : $e")
                    e.printStackTrace()
                    Toast.makeText(context, context.getString(R.string.no_access_to_DB), Toast.LENGTH_SHORT).show()
                    LoginManager.getInstance().logOut()
                    loaderDisplay.postValue(View.GONE)
                }
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,email,picture")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    fun facebookCancel() {
        Log.d(TAG, "onCancel: ")
        //TODO : remove this line when app is finished
        LoginManager.getInstance().logOut()
        loaderDisplay.postValue(View.GONE)
    }

    fun facebookError(exception: FacebookException) {
        Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onError: $exception")
        loaderDisplay.postValue(View.GONE)
    }

    private fun loginTreatment(response: ResponseFromApi) {
        val code = response.code
        val message = response.message
        if (code == SUCCESS) {
            val results = response.results
            loaderDisplay.postValue(View.GONE)
            if (connectedToGoogle) {
                googleBtnSwitch.postValue(false)
            }
            val name = results?.userName
            Log.d(TAG, "code = $code body = $response")
            Toast.makeText(context, String.format(context.getString(R.string.welcome, name)), Toast.LENGTH_SHORT).show()
            connectedUser = response.results!!
            val homeIntent = Intent(context, BottomActivity::class.java)
            resolveIntent.postValue(homeIntent)
        } else {
            Toast.makeText(context, String.format(context.getString(R.string.code_message), code, message), Toast.LENGTH_SHORT).show()
            loaderDisplay.postValue(View.GONE)
        }
    }

}