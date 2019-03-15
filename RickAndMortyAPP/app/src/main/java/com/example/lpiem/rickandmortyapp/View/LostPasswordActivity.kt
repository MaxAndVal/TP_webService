package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.LostPasswordManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.Util.observeOnce
import kotlinx.android.synthetic.main.activity_lost_password.*

class LostPasswordActivity : AppCompatActivity() {

    private lateinit var lostPasswordManager: LostPasswordManager
    private lateinit var isSendCodeObserver: Observer<Boolean>
    private lateinit var isLoginWithCodeObserver: Observer<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_password)
        lostPasswordManager = LostPasswordManager.getInstance(this)

        isSendCodeObserver = Observer {
            if (it) openCodeModal()
        }
        isLoginWithCodeObserver = Observer {
            if (it == 200) {
                finish()
            } else if (it == 204) {
                tv_errorInput.text = getString(R.string.ErrorInvalideCode)
                tv_errorInput.visibility = View.VISIBLE
            } else if (it == 205) {
                tv_errorInput.visibility = View.VISIBLE
                tv_errorInput.text = getString(R.string.ErrorCodeExpired)
            } else {
                tv_errorInput.visibility = View.VISIBLE
                tv_errorInput.text = getString(R.string.ErrorUnknow)
            }
        }


        lostPasswordManager.isSendCodeSucceded.observeOnce(isSendCodeObserver)
        lostPasswordManager.isLoginWithcode.observeOnce(isLoginWithCodeObserver)

        toggleView(false)

        tv_backToLogin.setOnClickListener { finish() }
        btn_sendCode.setOnClickListener { sendCode() }
        tv_alreadyCode.setOnClickListener { toggleView(true) }
        btn_enterCode.setOnClickListener { enterCode(et_enterCode.text.toString()) }

    }

    private fun enterCode(code: String) {
        if (!code.isEmpty()) {
            tv_errorInput.visibility = View.GONE
            lostPasswordManager.enterWithCode(code)
            layoutInputCode.setHelperTextColor(resources.getColorStateList(R.color.black))
        } else {
            tv_errorInput.visibility = View.VISIBLE
            tv_errorInput.text = getString(R.string.ErrorEmptyCode)
            layoutInputCode.setHelperTextColor(resources.getColorStateList(R.color.ErrorLightRed))
        }
    }

    private fun toggleView(isCodeSend: Boolean) {

        if (isCodeSend) {
            cl_codeSent.visibility = View.VISIBLE
            cl_codeIsNotSent.visibility = View.GONE
        } else {
            cl_codeSent.visibility = View.GONE
            cl_codeIsNotSent.visibility = View.VISIBLE
        }

    }

    private fun openCodeModal() {
        toggleView(true)
    }

    private fun sendCode() {
        tv_errorInputEmail.visibility = View.GONE
        val userEmail = ed_email.text.toString()
        if (!userEmail.isEmpty()) {
            lostPasswordManager.sendCodeManager(userEmail)
            toggleView(true)
            textInputEmail.setHelperTextColor(resources.getColorStateList(R.color.black))
        } else {
            tv_errorInputEmail.visibility = View.VISIBLE
            tv_errorInputEmail.text = getString(R.string.ErrorEmptyEmail)
            textInputEmail.setHelperTextColor(resources.getColorStateList(R.color.ErrorLightRed))
        }

    }

    override fun onResume() {
        super.onResume()
        tv_errorInput.visibility = View.GONE
        tv_errorInputEmail.visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
    }
}
