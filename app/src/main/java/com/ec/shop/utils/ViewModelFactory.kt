package com.ec.shop.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ec.shop.data.repositories.CartRepository
import com.ec.shop.ui.authentication.LoginViewModel
import com.ec.shop.ui.cart.CartViewModel
import com.ec.shop.ui.scan.ScanViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private var application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application = application) as T
        }
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            return ScanViewModel(
                application = application,
                cartRepository = CartRepository(application = application)
            ) as T
        }
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(
                application = application,
                cartRepository = CartRepository(application = application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
