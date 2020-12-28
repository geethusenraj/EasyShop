package com.ec.shop.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ec.shop.R
import com.ec.shop.constants.Constants.Companion.IS_LOGGED_IN
import com.ec.shop.constants.Constants.Companion.SPLASH_TIME_OUT
import com.ec.shop.data.preference.PreferenceHelper
import com.ec.shop.ui.authentication.LoginActivity
import com.ec.shop.utils.openActivity

class SplashScreen : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPref = PreferenceHelper.defaultPrefs(this)
        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = sharedPref.getBoolean(IS_LOGGED_IN, false)
            if (isLoggedIn)
                openActivity(HomeActivity::class.java)
            else
                openActivity(LoginActivity::class.java)
            finish()
        }, SPLASH_TIME_OUT)
    }
}