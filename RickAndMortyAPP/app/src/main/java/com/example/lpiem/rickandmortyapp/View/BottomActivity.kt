package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom.*

class BottomActivity : AppCompatActivity() {

    private var loginAppManager = LoginAppManager.getInstance(this)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                openFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_collection -> {
                message.setText(R.string.collection)
                openFragment(CollectionFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_social -> {
                openFragment(SocialFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                message.setText(R.string.profilSettings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        Log.d(TAG, "game beginned : ${loginAppManager.gameInProgress}")
        tv_wallet.text = String.format(getString(R.string.wallet_amount), loginAppManager.connectedUser?.userWallet, " ")
        tv_wallet.setOnLongClickListener { iAmPickleRick() }
        openFragment(HomeFragment())

    }

    override fun onBackPressed() {
        super.onBackPressed()
        loginAppManager.connectedUser = null
        loginAppManager.gameInProgress = true
        finish()
    }

    internal fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun iAmPickleRick(): Boolean {
        val toast = Toast.makeText(this, getString(R.string.pickle_rick), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 150, 90)
        toast.show()
        return true
    }

}
