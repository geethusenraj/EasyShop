package com.ec.shop.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ec.shop.R
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.ui.bill.BillFragment
import com.ec.shop.ui.cart.CartFragment
import kotlinx.android.synthetic.main.fragment_cart.view.tvProductName
import kotlinx.android.synthetic.main.fragment_cart.view.tvRate
import kotlinx.android.synthetic.main.row_item_bill.view.*
import kotlinx.android.synthetic.main.row_item_cart.view.*
import kotlinx.android.synthetic.main.row_item_cart.view.tvQuantity

class CartRecyclerViewAdapter(
    private val productList: ArrayList<CartEntity>,
    private val fragment: Fragment
) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(cartEntity: CartEntity, fragment: Fragment) {
            itemView.apply {

            }
            itemView.apply {

                tvProductName.text = cartEntity.name
                tvRate.text = "Rs.${cartEntity.rate}-/-"
                if (fragment is CartFragment) {
                    tvQuantity.text = cartEntity.quantity.toString()
                }
                if (fragment is BillFragment) {
                    tvQuantity.text = "${cartEntity.quantity}x"
                    tvCalc.text = "= Rs.${cartEntity.total} -/-"
                }
//                numberPicker.minValue = 1
//                numberPicker.maxValue = 10
//                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        lateinit var holder: DataViewHolder
        when (fragment) {
            is CartFragment -> holder = DataViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_cart, parent, false
                )
            )
            is BillFragment -> holder = DataViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_bill, parent, false
                )
            )
        }
        return holder
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(productList[position], fragment)
    }

    fun addCartData(cartEntity: List<CartEntity>) {
        this.productList.apply {
            clear()
            addAll(cartEntity)
        }

    }
}

