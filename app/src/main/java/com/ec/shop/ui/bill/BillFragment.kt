package com.ec.shop.ui.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ec.shop.R

class BillFragment : Fragment() {

    private lateinit var billViewModel: BillViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        billViewModel =
                ViewModelProviders.of(this).get(BillViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        billViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
