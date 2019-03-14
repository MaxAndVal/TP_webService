package com.example.lpiem.rickandmortyapp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.LostPasswordManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_lost_password.*

class LostPasswordActivity : AppCompatActivity() {

    private lateinit var lostPasswordManager: LostPasswordManager
    private lateinit var isSendCodeObserver : Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_password)
        lostPasswordManager = LostPasswordManager.getInstance(this)
        isSendCodeObserver = Observer {
            if(it)openCodeModal()
        }

        toggleView(false)

        tv_backToLogin.setOnClickListener { finish() }
        btn_sendCode.setOnClickListener{sendCode()}
        tv_alreadyCode.setOnClickListener { toggleView(true) }
    }

    private fun toggleView(isCodeSend: Boolean) {

        if(isCodeSend){
            cl_codeSent.visibility = View.VISIBLE
            cl_codeIsNotSent.visibility = View.GONE
        }else{
            cl_codeSent.visibility = View.GONE
            cl_codeIsNotSent.visibility = View.VISIBLE
        }

    }

    private fun openCodeModal() {
        Log.d(TAG,"coucou")
    }

    private fun sendCode() {
       var user_email = ed_email.text.toString()

        if(!user_email.isEmpty()){
            lostPasswordManager.isSendCodeSucceded.observeOnce(isSendCodeObserver)
            lostPasswordManager.sendCodeManager(user_email)
            toggleView(true)
        }else{
            Toast.makeText(this,"Merci de rentrer votre Email", Toast.LENGTH_LONG).show()
        }

    }
}
