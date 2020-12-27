package com.ec.shop.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ec.shop.R
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.listeners.ProjectEventListeners
import com.ec.shop.ui.adapters.CartRecyclerViewAdapter
import com.ec.shop.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*

class CartFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var viewModel: CartViewModel
    private lateinit var adapter: CartRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(
                application = requireActivity().application
            )
        ).get(CartViewModel::class.java)
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_cart, container, false)
        mView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val onListItemClick: ProjectEventListeners.OnListItemClick = object :
            ProjectEventListeners.OnListItemClick {
            override fun onClick(view: View?, position: Int, cartEntity: CartEntity) {
                viewModel.deleteCartItem(cartEntity = cartEntity)
            }

        }
        adapter = CartRecyclerViewAdapter(arrayListOf(), this@CartFragment, onListItemClick)
        mView.recyclerView.addItemDecoration(
            DividerItemDecoration(
                mView.recyclerView.context,
                (mView.recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        mView.recyclerView.adapter = adapter




        viewModel.productData.observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {
                mView.tvNoContent.visibility = View.GONE
                mView.recyclerView.visibility = View.VISIBLE
            } else {
                mView.recyclerView.visibility = View.GONE
                mView.tvNoContent.visibility = View.VISIBLE
            }
            adapter.apply {
                addCartData(it)
                notifyDataSetChanged()
            }
        })
        return mView.rootView
    }
}
