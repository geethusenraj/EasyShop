package com.ec.shop.data.repositories

import android.app.Application
import com.ec.shop.data.db.AppDataBase
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.data.db.models.Product

class CartRepository(private val application: Application) {

    private val cartDao = AppDataBase.getDatabase(application).getCartDao()

    fun saveUserData(product: Product) {
        cartDao.insertAll(CartEntity(product))
    }

}

