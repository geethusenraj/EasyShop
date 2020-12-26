package com.ec.shop.ui.bill

import android.app.Application
import androidx.lifecycle.ViewModel
import com.ec.shop.data.repositories.CartRepository

class BillViewModel(
    private val application: Application,
    private val cartRepository: CartRepository
) : ViewModel() {

}