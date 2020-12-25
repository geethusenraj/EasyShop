package com.ec.shop.data.db.dao

import androidx.room.*
import com.ec.shop.data.db.entities.CartEntity


@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartEntity: CartEntity): Long


    @Transaction
    suspend fun upsert(cartEntity: CartEntity) {
        val exist = getProductCount(cartEntity.name)
        when {
            exist >= 1 -> updateCart(cartEntity.name, exist + 1)
            else -> {
                cartEntity.quantity = 1
                insert(cartEntity)
            }
        }
    }

    @Query("SELECT Quantity FROM CartTable WHERE ProductName=:name")
    fun getProductCount(name: String?): Int


    @Query("UPDATE CartTable SET Quantity=:quantity WHERE ProductName =:name")
    suspend fun updateCart(name: String?, quantity: Int)

}