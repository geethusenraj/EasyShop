package com.ec.shop.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ec.shop.data.db.models.Product


@Entity(
    tableName = "CartTable"
)
class CartEntity {
    constructor(product: Product) : this() {
        this.name = product.productName
        this.rate = product.productRate

    }

    constructor()

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Long = 0

    @ColumnInfo(name = "ProductName")
    var name: String? = null

    @ColumnInfo(name = "ProductRate")
    var rate: Double = 0.0

    @ColumnInfo(name = "Quantity")
    var quantity: Int = 0

    @ColumnInfo(name = "Total")
    var total: Double = 0.0
}