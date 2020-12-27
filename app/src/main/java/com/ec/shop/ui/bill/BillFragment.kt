package com.ec.shop.ui.bill

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ec.shop.R
import com.ec.shop.constants.Constants
import com.ec.shop.ui.adapters.CartRecyclerViewAdapter
import com.ec.shop.ui.cart.CartViewModel
import com.ec.shop.utils.ViewModelFactory
import com.ec.shop.utils.showSnackBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_bill.*
import kotlinx.android.synthetic.main.fragment_bill.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.recyclerView
import pub.devrel.easypermissions.EasyPermissions


class BillFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var root: View
    private lateinit var bitmap: Bitmap
    private lateinit var viewModel: CartViewModel
    private lateinit var adapter: CartRecyclerViewAdapter


    companion object {
        const val WRITE_EXTERNAL_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(
                application = requireActivity().application
            )
        ).get(CartViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
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
            if (it.isNotEmpty()) {
                root.tvNoPreview.visibility = View.GONE
                root.layoutContent.visibility = View.VISIBLE
            } else {
                root.layoutContent.visibility = View.GONE
                root.tvNoPreview.visibility = View.VISIBLE
            }
            adapter.apply {
                addCartData(it)
                notifyDataSetChanged()
            }
            val billAmount = it.map { price -> price.total }.sum()
            root.tvTotalBill.text = "Rs.$billAmount-/-"
        })
        viewModel.updateStatus.observe(requireActivity(), Observer {

            val activity = requireActivity().application;

            when (it) {
                true -> {
                    if (activity != null && isAdded) {
                        root.rootView.showSnackBar(getString(R.string.bill_generated_success))
                        showEmailAlertDialog()
                    }
                }
                false -> root.showSnackBar(getString(R.string.bill_generated_failed))
            }
        })

        root.btBill.setOnClickListener {
            requestPermission()
        }

        return root
    }


    private fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private fun showEmailAlertDialog() {
        val alert = AlertDialog.Builder(requireContext())
        val mView: View = layoutInflater.inflate(R.layout.layout_custom_dialog, null)
        val cancel: Button = mView.findViewById<View>(R.id.cancelDialog) as Button
        val submit: Button = mView.findViewById<View>(R.id.submitDialog) as Button
        val etEmail: EditText =
            mView.findViewById<View>(R.id.scannedText) as EditText
        alert.setView(mView)
        val alertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        cancel.setOnClickListener { alertDialog.dismiss() }
        submit.setOnClickListener {
            if (!isValidEmail(etEmail.text.toString())) {
                etEmail.error = "Email is invalid."
            } else {
                etEmail.error = ""
                shareViaEmail(etEmail.text.toString())
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun initPdf() {
        bitmap = loadBitmapFromView(
            root.layoutTotal,
            root.layoutTotal.width,
            root.layoutTotal.height
        )

    }

    private fun loadBitmapFromView(
        recyclerView: LinearLayout,
        width: Int,
        height: Int
    ): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        recyclerView.draw(c)
        return b
    }

    private fun requestPermission() {
        initPdf()
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    val bitmapBill = viewModel.getScreenshotFromRecyclerView(
                        root.recyclerView,
                        root.layoutTotal,
                        bitmap
                    )
                    bitmapBill?.let { viewModel.createPdfFile(it) }
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            })
            .check()


//        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
//        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
//            val bitmapBill = viewModel.getScreenshotFromRecyclerView(
//                root.recyclerView,
//                root.layoutTotal,
//                bitmap
//            )
//            bitmapBill?.let { viewModel.createPdfFile(it) }
//        } else {
//            EasyPermissions.requestPermissions(
//                this, getString(R.string.write_permission_content),
//                WRITE_EXTERNAL_STORAGE, permission
//            )
//        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required_dialog_title))
            .setMessage(getString(R.string.write_permission_content))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                requestPermission()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults, this
        )
    }

    private fun shareViaEmail(email: String) {
        try {
            val root = viewModel.createPdfDirectory()
            val fileLocation =
                root?.absolutePath + "/" + Constants.PDF_BILL
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            val message = "File to be shared is ${Constants.PDF_BILL}."
            intent.putExtra(Intent.EXTRA_SUBJECT, "EC SHOP Bill Receipt")
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://$fileLocation"))
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.data = Uri.parse("mailto:$email")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            root.rootView.showSnackBar("is exception raises during sending mail$e")

        }
    }
}


