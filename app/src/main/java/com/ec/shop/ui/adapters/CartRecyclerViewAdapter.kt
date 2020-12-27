package com.ec.shop.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ec.shop.R
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.listeners.ProjectEventListeners.OnListItemClick
import com.ec.shop.ui.bill.BillFragment
import com.ec.shop.ui.cart.CartFragment
import com.ec.shop.utils.showSnackBar
import kotlinx.android.synthetic.main.fragment_cart.view.tvProductName
import kotlinx.android.synthetic.main.fragment_cart.view.tvRate
import kotlinx.android.synthetic.main.row_item_bill.view.*
import kotlinx.android.synthetic.main.row_item_cart.view.*
import me.himanshusoni.quantityview.QuantityView


class CartRecyclerViewAdapter(
    private val productList: ArrayList<CartEntity>,
    private val fragment: Fragment,
    var onListItemClick: OnListItemClick?
) :
    RecyclerView.Adapter<CartRecyclerViewAdapter.DataViewHolder>() {


    class DataViewHolder(
        itemView: View,
        private val onListItemClick: OnListItemClick?,
        private val productList: ArrayList<CartEntity>
    ) :
        RecyclerView.ViewHolder(itemView),
        QuantityView.OnQuantityChangeListener {

        @SuppressLint("SetTextI18n")
        fun bind(
            cartEntity: CartEntity, fragment: Fragment, position: Int,
            holder: DataViewHolder,
            onListItemClick: OnListItemClick
        ) {
            itemView.apply {

                tvProductName.text = cartEntity.name
                tvRate.text = "Rs.${cartEntity.rate}-/-"
                if (fragment is CartFragment) {
                    qtyView.quantity = cartEntity.quantity
                    qtyView.onQuantityChangeListener = this@DataViewHolder
                    layoutDelete.setOnClickListener {
                        onListItemClick.onClick(itemView, position, cartEntity)
                    }
                }
                if (fragment is BillFragment) {
                    tvQuantity.text = "${cartEntity.quantity}x"
                    tvCalc.text = "= Rs.${cartEntity.total} -/-"
                }
            }
        }

        override fun onQuantityChanged(
            oldQuantity: Int,
            newQuantity: Int,
            programmatically: Boolean
        ) {
            onListItemClick?.onQuantityChanged(
                cartEntity = productList[position],
                quantity = newQuantity
            )
        }

        override fun onLimitReached() {
            itemView.showSnackBar("Maximum limit reached.")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        lateinit var holder: DataViewHolder
        when (fragment) {
            is CartFragment -> {
                holder = DataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.row_item_cart, parent, false
                    ), onListItemClick, productList
                )
            }
            is BillFragment -> holder = DataViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_bill, parent, false
                ),
                onListItemClick,
                productList
            )
        }
        return holder
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        onListItemClick?.let { holder.bind(productList[position], fragment, position, holder, it) }
    }

    fun addCartData(cartEntity: List<CartEntity>) {
        this.productList.apply {
            clear()
            addAll(cartEntity)
        }

    }

    fun setClickListener(context: OnListItemClick) {
        this.onListItemClick = context;
    }

}

