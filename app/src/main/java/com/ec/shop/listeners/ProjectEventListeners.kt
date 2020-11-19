package com.ec.shop.listeners

interface ProjectEventListeners {

    interface LoginEvents {

        fun onRequestOTP()
        fun onVerifyOTP()

    }
}