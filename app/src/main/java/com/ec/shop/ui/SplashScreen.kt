package com.ec.shop.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ec.shop.R
import com.ec.shop.constants.Constants.Companion.SPLASH_TIME_OUT
import com.ec.shop.utils.openActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            openActivity(HomeActivity::class.java)
            finish()
        }, SPLASH_TIME_OUT)
    }
}