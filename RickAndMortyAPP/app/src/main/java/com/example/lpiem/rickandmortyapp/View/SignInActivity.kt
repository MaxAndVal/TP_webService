package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.SignInManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_signin_activity.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInManager: SignInManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_activity)

        signInManager = SignInManager.getInstance(this)
        tv_alreadyAccount.setOnClickListener { signInManager.goBack() }
        btn_confSignIn.setOnClickListener { signInManager.signIn() }

    }

}