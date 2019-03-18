package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.Manager.KaamelottManager
import com.example.lpiem.rickandmortyapp.Manager.LoginAppManager
import com.example.lpiem.rickandmortyapp.Manager.settings.SettingsManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.OpenDeck.OpenDeckActivity
import com.example.lpiem.rickandmortyapp.View.Settings.PasswordFragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.Shop.ShopActivity
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom.*


class BottomActivity : AppCompatActivity() {

    private var loginAppManager = LoginAppManager.getInstance(this)
    private var settingsManager = SettingsManager.getInstance(this)
    private var doubleBackToExitPressedOnce = false
    private lateinit var openFaqFragmentObserver: Observer<Fragment>
    private lateinit var openFragChangePassObserver: Observer<PasswordFragment>

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                tv_message.setText(R.string.title_home)
                openFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_collection -> {
                tv_message.setText(R.string.collection)
                openFragment(CollectionFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_social -> {
                tv_message.text = getString(R.string.titleSocial)
                openFragment(SocialFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                tv_message.text = getString(R.string.titleSettings)
                openFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)

        openFaqFragmentObserver = Observer {
            openFragmentFAQ(it)
        }

        openFragChangePassObserver = Observer {
            openFragmentChangePassword(it)
        }

        settingsManager.openFaqLiveData.observeForever(openFaqFragmentObserver)
        settingsManager.openFragChangePassLiveData.observeForever(openFragChangePassObserver)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        tv_wallet.text = String.format(getString(R.string.wallet_amount), loginAppManager.connectedUser?.userWallet, " ")
        tv_wallet.setOnLongClickListener { iAmPickleRick() }
        tv_wallet.setOnClickListener { openShop() }
        tv_deckToOpen.setOnClickListener { openDeck(loginAppManager.connectedUser!!.deckToOpen!!) }
        openFragment(HomeFragment())
    }

    private fun openDeck(deckToOpen: Int) {
        if (deckToOpen > 0) {
            val openIntent = Intent(this, OpenDeckActivity::class.java)
            startActivity(openIntent)
        }
    }

    private fun openFragmentFAQ(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) fragmentManager.popBackStackImmediate()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flMain, fragment).addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        flMain.bringToFront()
        tv_deckToOpen.visibility = View.GONE
        tv_wallet.visibility = View.GONE
        navigation.visibility = View.GONE
        fragmentLayout.visibility = View.GONE
    }

    private fun openFragmentChangePassword(passwordFragment: PasswordFragment) {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) fragmentManager.popBackStackImmediate()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flMain, passwordFragment).addToBackStack(null)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        flMain.bringToFront()
        tv_deckToOpen.visibility = View.GONE
        tv_wallet.visibility = View.GONE
        navigation.visibility = View.GONE
        fragmentLayout.visibility = View.GONE
        tv_message.visibility = View.GONE
    }

    override fun onBackPressed() {
        val backStackLength = supportFragmentManager.backStackEntryCount
        Log.d(TAG, "backStackLength for BottomActivity : $backStackLength")

        // handling double tap to exit app
        if (backStackLength == 0 && doubleBackToExitPressedOnce) {
            loginAppManager.connectedUser = null
            loginAppManager.gameInProgress = true
            settingsManager.openFaqLiveData.removeObserver(openFaqFragmentObserver)
            settingsManager.openFragChangePassLiveData.removeObserver(openFragChangePassObserver)
            clearGame()
            super.onBackPressed()
            return
        } else {
            supportFragmentManager.popBackStack()
            navigation.visibility = VISIBLE
        }

        if (backStackLength == 0) {
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.double_back_to_quit_app), Toast.LENGTH_SHORT).show()
        }


        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun iAmPickleRick(): Boolean {
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.piclke_rick_sound)
        mediaPlayer?.start()
        val toast = Toast.makeText(this, getString(R.string.pickle_rick), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 150, 90)
        toast.show()
        return true
    }

    private fun clearGame() {
        val kaamelottManager = KaamelottManager.getInstance(this)
        kaamelottManager.turn = 0
        kaamelottManager.score = 0
        finish()
    }

    private fun openShop() {
        val shopIntent = Intent(this@BottomActivity, ShopActivity::class.java)
        startActivity(shopIntent)
    }

    fun seekAndDestroy() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        loginAppManager.connectedUser = null
        loginAppManager.gameInProgress = true
        settingsManager.openFaqLiveData.removeObserver(openFaqFragmentObserver)
        settingsManager.openFragChangePassLiveData.removeObserver(openFragChangePassObserver)
        clearGame()
        onDestroy()
    }

    override fun onResume() {
        super.onResume()
        tv_deckToOpen.setOnClickListener {
            val deckToOpen = loginAppManager.connectedUser?.deckToOpen
            if (deckToOpen != null) {
                tv_deckToOpen.text = deckToOpen.toString()
                openDeck(deckToOpen)
            }
        }
        tv_wallet.text = String.format(getString(R.string.wallet_amount), loginAppManager.connectedUser?.userWallet, " ")
        tv_deckToOpen.text = loginAppManager.connectedUser?.deckToOpen.toString()
    }


}
