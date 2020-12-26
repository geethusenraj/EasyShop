package com.ec.shop.ui.scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ec.shop.data.db.models.Product
import com.ec.shop.data.repositories.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScanViewModel(
    private val application: Application,
    private val cartRepository: CartRepository
) : ViewModel() {


    fun addProductToDb(qrResult: String) {
        when {
            qrResult.contains("*") -> {
                val textSplit = qrResult.split("*")
                viewModelScope.launch(Dispatchers.IO) {
                    cartRepository.saveUserData(Product(textSplit[1], textSplit[2].toDouble()))
                }
            }
        }

    }
}