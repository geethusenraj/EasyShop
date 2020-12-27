package com.ec.shop.listeners

import android.view.View
import com.ec.shop.data.db.entities.CartEntity

interface ProjectEventListeners {

    interface LoginEvents {

        fun onRequestOTP()
        fun onVerifyOTP()

    }

    interface OnListItemClick {
        fun onClick(view: View?, position: Int, cartEntity: CartEntity)
    }
}