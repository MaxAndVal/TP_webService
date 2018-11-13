package com.example.lpiem.rickandmortyapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String token;
    public static final String TAG = "TAG_Magic";
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    public static final int RC_SIGN_IN = 1;
    private TextView userNameTV;
    private Button disconnectGoogleBtn;
    private String userNameGG;
    private String userNameFB;
    private GoogleSignInAccount account;
    private Intent dispalyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dispalyIntent = new Intent(MainActivity.this, DisplayActivity.class);


        userNameTV = findViewById(R.id.userNameTV);
        userNameTV.setVisibility(View.INVISIBLE);
        disconnectGoogleBtn = findViewById(R.id.disconnectGoogle);
        disconnectGoogleBtn.setVisibility(View.INVISIBLE);
        disconnectGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectGoogleAccount();
            }
        });


        callbackManager = CallbackManager.Factory.create();

        Log.d(TAG, "onCreate: callBackManager = " + callbackManager);

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("public_profile");

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        // FACEBOOK
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Callback registration
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        token = loginResult.getAccessToken().getToken();
                        Log.d(TAG, "onSuccess: token = " + token);

                        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                        if (isLoggedIn) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                                JSONObject result = response.getJSONObject();
                                            try {
                                                userNameFB = result.getString("name");
                                                userNameTV.setText(userNameFB);
                                                userNameTV.setVisibility(View.VISIBLE);
                                                Log.d(TAG, "onCompleted: name = " + userNameFB);
                                                Toast.makeText(getApplicationContext(), "Bienvenue " + userNameFB, Toast.LENGTH_SHORT).show();
                                                startActivity(dispalyIntent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: " + exception.toString());
                    }
                });

            }
        });


        // GOOGLE
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            signInButton.setVisibility(View.INVISIBLE);
            disconnectGoogleBtn.setVisibility(View.VISIBLE);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                userNameGG = account.getDisplayName();
                userNameTV.setText(userNameGG);
                userNameTV.setVisibility(View.VISIBLE);
            } else {
                userNameTV.setVisibility(View.INVISIBLE);
            }
            startActivity(dispalyIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        if (account != null) {
            userNameGG = account.getDisplayName();
            userNameTV.setText(userNameGG);
            userNameTV.setVisibility(View.VISIBLE);
        } else {
            userNameTV.setVisibility(View.INVISIBLE);
        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "handleSignInResult: connected" );
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void disconnectGoogleAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                task.getResult();
                Log.d(TAG, "onComplete: disconnectGoogle");
                //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account != null) {
                    userNameGG = account.getDisplayName();
                    userNameTV.setText(userNameGG);
                    userNameTV.setVisibility(View.VISIBLE);
                    userNameTV.setText(R.string.empty);
                } else {
                    userNameTV.setVisibility(View.INVISIBLE);

                }
            }
        });
        signInButton.setVisibility(View.VISIBLE);
        disconnectGoogleBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
