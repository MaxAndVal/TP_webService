package com.example.lpiem.rickandmortyapp.View

import android.os.Bundle
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

    private lateinit var loginAppManager: LoginAppManager

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

        loginAppManager = LoginAppManager.getInstance(this)
        tv_wallet.text = "${loginAppManager.connectedUser.userWallet} $ "
        tv_wallet.setOnLongClickListener { iAmPickleRick() }
        openFragment(HomeFragment())

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }

    fun iAmPickleRick(): Boolean {
        Toast.makeText(this, "I'm piclke Rick !!!", Toast.LENGTH_SHORT).show()
        return true
    }

}
