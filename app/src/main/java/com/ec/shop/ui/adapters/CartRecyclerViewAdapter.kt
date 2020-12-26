package com.ec.shop.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ec.shop.R
import com.ec.shop.data.db.entities.CartEntity
import kotlinx.android.synthetic.main.fragment_cart.view.tvProductName
import kotlinx.android.synthetic.main.fragment_cart.view.tvRate
import kotlinx.android.synthetic.main.row_item_cart.view.*

class CartRecyclerViewAdapter(private val productList: ArrayList<CartEntity>) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(cartEntity: CartEntity) {
            itemView.apply {

            }
            itemView.apply {
                tvProductName.text = cartEntity.name
                tvRate.text = cartEntity.rate
                tvQuantity.text = cartEntity.quantity.toString()
//                numberPicker.minValue = 1
//                numberPicker.maxValue = 10
//                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_cart, parent, false)
        )

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    fun addCartData(cartEntity: List<CartEntity>) {
        this.productList.apply {
            clear()
            addAll(cartEntity)
        }

    }
}

