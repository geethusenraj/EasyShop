package com.ec.shop.ui.authentication

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ec.shop.constants.Constants.Companion.OTP_SENT
import com.ec.shop.constants.Constants.Companion.TIME_OUT
import com.ec.shop.constants.Constants.Companion.VERIFICATION_COMPLETED
import com.ec.shop.constants.Constants.Companion.VERIFICATION_FAILED
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.coroutines.*
import java.text.DecimalFormat


class LoginViewModel(private val application: Application) : ViewModel() {

    private var job: Deferred<Unit>? = null
    private var verificationCode: String = ""
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _otpStatus = MutableLiveData<String>()
    val otpStatus: LiveData<String>
        get() = _otpStatus

    private val _updateKeyboard = MutableLiveData<Boolean>()
    val updateKeyboard: LiveData<Boolean>
        get() = _updateKeyboard

    private val _otpTimer = MutableLiveData<String>()
    val otpTimer: LiveData<String>
        get() = _otpTimer

    private val _sendOtpEventAction = MutableLiveData<Any>()
    val sendOtpEventAction: LiveData<Any>
        get() = _sendOtpEventAction

    private val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String>
        get() = _currentUser

    fun startFirebaseLogin() =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                _otpStatus.value = VERIFICATION_COMPLETED
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _otpStatus.value = VERIFICATION_FAILED + "$e"
            }

            override fun onCodeSent(
                code: String,
                forceResendingToken: ForceResendingToken
            ) {
                super.onCodeSent(code, forceResendingToken)
                verificationCode = code
                _otpStatus.value = OTP_SENT
                job = if (job == null || job!!.isCancelled)
                    countDownAsync()
                else {
                    job!!.cancel()
                    countDownAsync()
                }
            }

        }

    private fun countDownAsync() = GlobalScope.async(Dispatchers.IO) {
        Log.e("TIMER_", "Start")
        TIME_OUT
        repeat(TIME_OUT + 1) { i ->
            val res = DecimalFormat("00").format(TIME_OUT - i)
            Log.e("TIMER_", "job: I'm sleeping $i ...")
            _otpTimer.value = "00:$res"
            delay(500L)
        }
        delay(1300L)
        println()
    }

//    private fun countDown() = GlobalScope.async(Dispatchers.IO) {
//        _updateKeyboard.value = true
//
//        repeat(TIME_OUT + 1) {
//            val res = DecimalFormat("00").format(TIME_OUT - it)
//            println("Kotlin Coroutines World! $res")
//            withContext(Dispatchers.Main) {
//                _otpTimer.value = "00:$res"
//            }
//            delay(1000)
//        }
//        println("finished")
//
//    }

    fun sendOtp(text: String) {
        if (text.isNotEmpty()) {
            _updateKeyboard.value = true
            _sendOtpEventAction.value = Any()
        } else {
            _updateKeyboard.value = true
            _otpStatus.value = "Please type phone number or pick from list"
        }
    }

    fun setPhoneNumber() {
        val user = auth.currentUser
        try {
            _currentUser.value = user?.phoneNumber
        } catch (e: Exception) {
            _otpStatus.value = "Phone number not found"
            _currentUser.value = "---"
        }
    }

    fun verifyOtp(otpValue: String) {
        _updateKeyboard.value = true
        if (otpValue.isNotEmpty()) {
            val credential =
                PhoneAuthProvider.getCredential(verificationCode, otpValue)
            signInWithPhone(credential)
        } else {
            _otpStatus.value = "Please type OTP number"
        }
    }

    private fun signInWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _otpStatus.value = "Correct OTP"
                    if (job!!.isActive)
                        job!!.cancel()
                    setPhoneNumber()
                } else {
                    _otpStatus.value = "Incorrect OTP"
                }
            }
    }

}

