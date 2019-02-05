package com.example.lpiem.rickandmortyapp.View

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lpiem.rickandmortyapp.Presenter.HomeManager
import com.example.lpiem.rickandmortyapp.Presenter.LoginAppManager
import com.example.lpiem.rickandmortyapp.R
import com.example.lpiem.rickandmortyapp.View.Collection.list.CollectionFragment
import com.example.lpiem.rickandmortyapp.View.Home.HomeFragment
import com.example.lpiem.rickandmortyapp.View.Settings.SettingsFragment
import com.example.lpiem.rickandmortyapp.View.Shop.ShopActivity
import com.example.lpiem.rickandmortyapp.View.Social.SocialFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom.*
import java.io.IOException

class BottomActivity : AppCompatActivity() {

    private var loginAppManager = LoginAppManager.getInstance(this)

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
                tv_message.text = "Social"
                openFragment(SocialFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                tv_message.text = "Settings"
                openFragment(SettingsFragment())
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
        tv_wallet.setOnClickListener { openShop() }
        var deckToOpen = loginAppManager.connectedUser!!.deckToOpen;
            tv_deckToOpen.text = deckToOpen.toString()
        tv_deckToOpen.setOnClickListener { openDeck(deckToOpen!!) }

        openFragment(HomeFragment())

    }

    private fun openDeck(deckToOpen: Int) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        loginAppManager.connectedUser = null
        loginAppManager.gameInProgress = true
        clearGame()
    }

    internal fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun iAmPickleRick(): Boolean {
        val mp = MediaPlayer ()
        try {
            mp.setDataSource ("http://peal.io/download/a9r7j")
            mp.prepare ()
            mp.start ()
        } catch (e: IOException) {
            Toast.makeText (this, "The file does not exist", Toast.LENGTH_LONG) .show ()
        }
        val toast = Toast.makeText(this, getString(R.string.pickle_rick), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 150, 90)
        toast.show()
        return true
    }

    private fun clearGame() {
        val homeManager = HomeManager.getInstance(this)
        homeManager.turn = 0
        homeManager.score =0
        finish()
    }

    private fun openShop() {
        val shopIntent = Intent(this@BottomActivity, ShopActivity::class.java)
        startActivity(shopIntent)
    }

    override fun onResume() {
        super.onResume()
        tv_wallet.text = String.format(getString(R.string.wallet_amount), loginAppManager.connectedUser?.userWallet, " ")
    }


}
