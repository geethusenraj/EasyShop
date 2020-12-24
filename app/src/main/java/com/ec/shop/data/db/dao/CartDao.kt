package com.ec.shop.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.ec.shop.data.db.entities.CartEntity


@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cartEntity: CartEntity)
}