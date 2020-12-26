package com.ec.shop.ui.cart

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.data.repositories.CartRepository

class CartViewModel(
    private val application: Application,
    private val cartRepository: CartRepository
) : ViewModel() {

    var productData: LiveData<List<CartEntity>> = MutableLiveData()


    init {
        productData = cartRepository.getData()
    }
//    fun getProductData() {
//        productData.value= cartRepository.getData()
//        viewModelScope.launch(Dispatchers.IO) {
//            _productData.postValue(cartRepository.getData())
//        }

//    }


}