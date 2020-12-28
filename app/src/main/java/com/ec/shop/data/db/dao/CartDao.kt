package com.ec.shop.data.db.dao

import androidx.lifecycle.LiveData
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
            exist >= 1 -> updateCart(cartEntity.name, exist + 1, (exist + 1) * (cartEntity.rate))
            else -> {
                cartEntity.quantity = 1
                cartEntity.total = cartEntity.quantity * cartEntity.rate
                insert(cartEntity)
            }
        }
    }

    @Query("SELECT Quantity FROM CartTable WHERE ProductName=:name")
    fun getProductCount(name: String?): Int


    @Query("UPDATE CartTable SET Quantity=:quantity, Total=:total WHERE ProductName =:name")
    suspend fun updateCart(name: String?, quantity: Int, total: Double)

    @Query("SELECT * FROM CartTable")
    fun getData(): LiveData<List<CartEntity>>

    @Query("DELETE FROM CartTable WHERE ProductName=:name")
    suspend fun deleteItem(name: String)

    @Query("DELETE FROM CartTable")
    suspend fun clearDb()
}