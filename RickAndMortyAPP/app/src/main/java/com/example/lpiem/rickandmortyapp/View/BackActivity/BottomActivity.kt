package com.example.lpiem.rickandmortyapp.View.BackActivity

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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.Connection.LoginActivity
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.Settings.PasswordFragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.example.lpiem.rickandmortyapp.ViewModel.Connection.LoginAppManager
import com.example.lpiem.rickandmortyapp.ViewModel.Home.KaamelottManager
import com.example.lpiem.rickandmortyapp.ViewModel.Market.MarketManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.ChangePasswordManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.FaqManager
import com.example.lpiem.rickandmortyapp.ViewModel.settings.SettingsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom.*


class BottomActivity : AppCompatActivity() {

    private var loginAppManager = LoginAppManager.getInstance(this)
    private var settingsManager = SettingsManager.getInstance(this)
    private var changePasswordManager = ChangePasswordManager.getInstance(this)
    private var faqManager = FaqManager.getInstance(this)
    private var marketManager = MarketManager.getInstance(this)
    private var doubleBackToExitPressedOnce = false
    private lateinit var openFaqFragmentObserver: Observer<Fragment>
    private lateinit var openFragChangePassObserver: Observer<PasswordFragment>
    private lateinit var closeFragPassObserver: Observer<Fragment>
    private lateinit var closeFaqFragObserver: Observer<Fragment>
    private lateinit var updateUserInfoObserver: Observer<Int>
    private lateinit var fragmentManager: FragmentManager
    private lateinit var disconnectObserver: Observer<Boolean>


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

        fragmentManager = supportFragmentManager

        openFaqFragmentObserver = Observer {
            openFragmentFAQ(it)
        }

        openFragChangePassObserver = Observer {
            openFragmentChangePassword(it)
        }

        closeFragPassObserver = Observer {
            closeChangePassword(it)
        }

        closeFaqFragObserver = Observer {
            closeFAQ(it)
        }

        updateUserInfoObserver = Observer {
            updateUserInfo(it)
        }

        disconnectObserver = Observer {
            if (loginAppManager.connectedUser == null) seekAndDestroy()
        }

        settingsManager.openFaqLiveData.observeForever(openFaqFragmentObserver)
        settingsManager.openFragChangePassLiveData.observeForever(openFragChangePassObserver)
        settingsManager.disconnect.observeForever(disconnectObserver)
        changePasswordManager.closeFragPassLiveData.observeForever(closeFragPassObserver)
        faqManager.closeFaqLiveData.observeForever(closeFaqFragObserver)
        marketManager.updateUserInfoLiveData.observeForever(updateUserInfoObserver)


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        openFragment(HomeFragment())
    }

    private fun updateUserInfo(it: Int?) {
        Log.d("TEST", it.toString())

        loginAppManager.connectedUser?.userWallet = it
    }

    private fun openDeck(deckToOpen: Int) {
        if (deckToOpen > 0) {
            val openIntent = Intent(this, OpenDeckActivity::class.java)
            startActivity(openIntent)
        }
    }

    private fun openFragmentFAQ(fragment: Fragment) {
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

    private fun closeChangePassword(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        tv_deckToOpen.visibility = View.VISIBLE
        tv_wallet.visibility = View.VISIBLE
        navigation.visibility = View.VISIBLE
        fragmentLayout.visibility = View.VISIBLE
        tv_message.visibility = View.VISIBLE
        fragmentManager.popBackStackImmediate()
    }

    private fun closeFAQ(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
        tv_deckToOpen.visibility = View.VISIBLE
        tv_wallet.visibility = View.VISIBLE
        navigation.visibility = View.VISIBLE
        fragmentLayout.visibility = View.VISIBLE
        fragmentManager.popBackStackImmediate()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) fragmentManager.popBackStackImmediate()

        val backStackLength = supportFragmentManager.backStackEntryCount
        Log.d(com.example.lpiem.rickandmortyapp.View.Connection.TAG, "backStackLength for BottomActivity : $backStackLength")

        // handling double tap to exit app
        if (backStackLength == 0 && doubleBackToExitPressedOnce) {
            loginAppManager.connectedUser = null
            loginAppManager.gameInProgress = true
            settingsManager.openFaqLiveData.removeObserver(openFaqFragmentObserver)
            settingsManager.openFragChangePassLiveData.removeObserver(openFragChangePassObserver)
            settingsManager.disconnect.removeObserver(disconnectObserver)
            changePasswordManager.closeFragPassLiveData.removeObserver(closeFragPassObserver)
            faqManager.closeFaqLiveData.removeObserver(closeFaqFragObserver)
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

    private fun seekAndDestroy() {
        Log.d("TEST", "seek and destroy")
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        loginAppManager.connectedUser = null
        loginAppManager.gameInProgress = true
        settingsManager.openFaqLiveData.removeObserver(openFaqFragmentObserver)
        settingsManager.openFragChangePassLiveData.removeObserver(openFragChangePassObserver)
        settingsManager.disconnect.removeObserver(disconnectObserver)
        changePasswordManager.closeFragPassLiveData.removeObserver(closeFragPassObserver)
        faqManager.closeFaqLiveData.removeObserver(closeFaqFragObserver)
        val kaamelottManager = KaamelottManager.getInstance(this)
        kaamelottManager.turn = 0
        kaamelottManager.score = 0
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        fragmentManager = supportFragmentManager
        tv_deckToOpen.setOnClickListener {
            val deckToOpen = loginAppManager.connectedUser?.deckToOpen
            if (deckToOpen != null) {
                tv_deckToOpen.text = deckToOpen.toString()
                openDeck(deckToOpen)
            }
        }
        tv_deckToOpen.text = loginAppManager.connectedUser?.deckToOpen.toString()
        tv_wallet.text = String.format(getString(R.string.wallet_amount), loginAppManager.connectedUser?.userWallet, " ")
        tv_wallet.setOnLongClickListener { iAmPickleRick() }
        tv_wallet.setOnClickListener { openShop() }
        tv_deckToOpen.setOnClickListener { openDeck(loginAppManager.connectedUser!!.deckToOpen!!) }
    }


}
