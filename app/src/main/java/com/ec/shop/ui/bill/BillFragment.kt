package com.ec.shop.ui.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ec.shop.R
import com.ec.shop.ui.adapters.CartRecyclerViewAdapter
import com.ec.shop.ui.cart.CartViewModel
import com.ec.shop.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_cart.view.*

class BillFragment : Fragment() {

    private lateinit var root: View
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_bill, container, false)
        root.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartRecyclerViewAdapter(arrayListOf(), this@BillFragment)
        root.recyclerView.adapter = adapter

        viewModel.productData.observe(requireActivity(), Observer {
            adapter.apply {
                addCartData(it)
                notifyDataSetChanged()
            }
        })

        return root
    }
}
