package com.ec.shop.ui.authentication

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ec.shop.R
import com.ec.shop.constants.Constants
import com.ec.shop.databinding.ActivityLoginBinding
import com.ec.shop.listeners.ProjectEventListeners
import com.ec.shop.ui.BaseActivity
import com.ec.shop.utils.ViewModelFactory
import com.ec.shop.utils.hideKeyboard
import com.ec.shop.utils.showSnackBar
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {

    private lateinit var mBinder: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinder = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinder.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(application = application))
            .get(LoginViewModel::class.java)
        viewModel.startFirebaseLogin()
        val adapter = ArrayAdapter(
            this, R.layout.item_drop_down,
            resources.getStringArray(R.array.phone_number_content)
        )
        defineObservers()


        mBinder.tvAutocomplete.setAdapter(adapter)

        viewModel.setPhoneNumber()

        mBinder.eventListeners = object : ProjectEventListeners.LoginEvents {
            override fun onRequestOTP() {
                viewModel.sendOtp(mBinder.tvAutocomplete.text.toString())
            }

            override fun onVerifyOTP() {
                viewModel.verifyOtp(mBinder.etOtp.text.toString())
            }

        }
    }

    private fun defineObservers() {
        viewModel.updateKeyboard.observe(this, Observer {
            if (it)
                hideKeyboard()
        })
        viewModel.otpStatus.observe(this, Observer {
            mBinder.root.showSnackBar(it)
        })
        viewModel.otpTimer.observe(this, Observer {
            Log.e("TIMER_it", it)
            mBinder.tvTimeRemains.text = it
        })
        viewModel.currentUser.observe(this, Observer {
            mBinder.tvCurrentUser.text = it
        })
        viewModel.sendOtpEventAction.observe(this, Observer {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mBinder.tvAutocomplete.text.toString(),
                Constants.TIME_OUT.toLong(),
                TimeUnit.SECONDS,
                this,
                viewModel.startFirebaseLogin()
            )
        })
    }

}


