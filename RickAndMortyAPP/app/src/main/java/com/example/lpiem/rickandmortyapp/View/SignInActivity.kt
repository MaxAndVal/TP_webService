package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lpiem.rickandmortyapp.Presenter.SignInManager
import com.example.lpiem.rickandmortyapp.R
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInManager: SignInManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signInManager = SignInManager.getInstance(this)
        tv_alreadyAccount.setOnClickListener { onBackPressed() }
        btn_confSignIn.setOnClickListener { signInManager.signIn() }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
