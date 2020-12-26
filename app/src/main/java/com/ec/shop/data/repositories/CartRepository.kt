package com.ec.shop.data.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.ec.shop.data.db.AppDataBase
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.data.db.models.Product

class CartRepository(application: Application) {

    private val cartDao = AppDataBase.getDatabase(application).getCartDao()

    suspend fun saveUserData(product: Product) {
        cartDao.upsert(CartEntity(product))
    }

    fun getData(): LiveData<List<CartEntity>> {
        return cartDao.getData()
    }

}

