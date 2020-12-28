package com.ec.shop.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ec.shop.R
import com.ec.shop.constants.Constants
import com.ec.shop.data.preference.PreferenceHelper
import com.ec.shop.data.preference.PreferenceHelper.set
import com.ec.shop.data.repositories.CartRepository
import com.ec.shop.ui.authentication.LoginActivity
import com.ec.shop.utils.openActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeActivity : BaseActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = PreferenceHelper.defaultPrefs(this)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == R.id.action_logout) {
            CoroutineScope(Dispatchers.IO).launch {
                CartRepository(this@HomeActivity.application).clearDB()
                sharedPref[Constants.IS_LOGGED_IN] = false
                openActivity(LoginActivity::class.java)
            }
            true
        } else super.onOptionsItemSelected(item)
    }
}
