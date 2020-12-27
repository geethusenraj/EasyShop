package com.ec.shop.ui.bill

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ec.shop.R
import com.ec.shop.ui.adapters.CartRecyclerViewAdapter
import com.ec.shop.ui.cart.CartViewModel
import com.ec.shop.utils.PDFGenerator
import com.ec.shop.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_bill.*
import kotlinx.android.synthetic.main.fragment_bill.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.recyclerView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class BillFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var root: View
    private lateinit var bitmap: Bitmap
    private lateinit var viewModel: CartViewModel
    private lateinit var pdfGenerator: PDFGenerator
    private lateinit var adapter: CartRecyclerViewAdapter


    companion object {
        const val WRITE_EXTERNAL_STORAGE = 2
        const val READ_EXTERNAL_STORAGE = 3
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

//        pdfGenerator = PDFGenerator(root.recyclerView)
//        val rvBitmap = pdfGenerator.getScreenshotFromRecyclerView(root.recyclerView);
//        pdfGenerator.createPdfFile(rvBitmap, requireActivity())
        viewModel.productData.observe(requireActivity(), Observer {
            adapter.apply {
                addCartData(it)
                notifyDataSetChanged()
            }
            val billAmount = it.map { price -> price.total }.sum()
            root.tvTotalBill.text = "Rs.$billAmount-/-"

        })

        root.btBill.setOnClickListener {
            requestPermission()
        }

        return root
    }

    private fun initPdf() {
//        Log.d("size", " " + root.rootLayout.width + "  " + root.rootLayout.height);
        bitmap = loadBitmapFromView(
            root.layoutTotal,
            root.layoutTotal.width,
            root.layoutTotal.height
        )

//        createPdf()

    }

//    private fun createPdf() {
//        //  Display display = wm.getDefaultDisplay();
//
//        val windowManager = requireActivity().getSystemService(WINDOW_SERVICE) as WindowManager
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels.toFloat()
//        val width = displayMetrics.widthPixels.toFloat()
//        val convertHeight = height.toInt()
//        val convertWidth = width.toInt()
//        val document = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create()
//        val page = document.startPage(pageInfo)
//        val canvas: Canvas = page.canvas
//        val paint = Paint()
//        canvas.drawPaint(paint)
//        bitmap =
//            Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true)
//        paint.color = Color.BLUE
//        canvas.drawBitmap(bitmap, 0f, 0f, null)
//        document.finishPage(page)
//
//        // write the document content
//        val targetPdf = "/sdcard/pdffromlayout.pdf"
//        val filePath: File
//        filePath = File(targetPdf)
//        try {
//            document.writeTo(FileOutputStream(filePath))
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(requireContext(), "Something wrong: $e", Toast.LENGTH_LONG)
//                .show()
//        }
//
//        // close the document
//        document.close()
//        Toast.makeText(requireContext(), "PDF is created!!!", Toast.LENGTH_SHORT).show()
//        openGeneratedPDF()
//    }

//    private fun openGeneratedPDF() {
//        val file = File("/sdcard/pdffromlayout.pdf")
//        if (file.exists()) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            val uri: Uri = Uri.fromFile(file)
//            val uri = FileProvider.getUriForFile(
//                requireContext(),
//                requireContext().applicationContext.packageName.toString() + ".provider",
//                file
//            )
//
//            intent.setDataAndType(uri, "application/pdf")
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            try {
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                Toast.makeText(
//                    requireContext(),
//                    "No Application available to view pdf",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }

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

    @SuppressLint("ObsoleteSdkInt")
    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
    private fun requestPermission() {
        initPdf()
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            val pdfGenerator = PDFGenerator(root.recyclerView)
            val bitmapBill = pdfGenerator.getScreenshotFromRecyclerView(
                root.recyclerView,
                root.layoutTotal,
                bitmap, requireActivity()
            )
            pdfGenerator.createPdfFile(bitmapBill, requireActivity())
//            initPdf()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.write_permission_content),
                WRITE_EXTERNAL_STORAGE, permission
            )
        }
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
}


