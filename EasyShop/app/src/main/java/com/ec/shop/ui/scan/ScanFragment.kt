package com.ec.shop.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ec.shop.R
import com.ec.shop.utils.showSnackBar
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ScanFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var beepManager: BeepManager
    private lateinit var scannerView: DecoratedBarcodeView
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scannerView = view.findViewById(R.id.QRScannerView)

        val formats = mutableListOf(BarcodeFormat.QR_CODE)
        beepManager = BeepManager(requireActivity())
        scannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        scannerView.setStatusText("")

        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    beepManager.isBeepEnabled = false
                    beepManager.playBeepSoundAndVibrate()

                    showConfirmationDialog(result.text)
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })

        checkPermissionsAndStartQRScan()
    }

    private fun showConfirmationDialog(qrData: String?) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_custom_view, null)

        val tvQrData = dialogView.findViewById<TextView>(R.id.qrContent)
        tvQrData.text = qrData
        val btnScanAgain: Button = dialogView.findViewById(R.id.btnScanAgain)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        val btnAdd: Button = dialogView.findViewById(R.id.btnOk)
        btnScanAgain.setOnClickListener {
            alertDialog.cancel()
        }
        btnAdd.setOnClickListener {
            it.showSnackBar("Added")
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create();
        alertDialog.window!!.getAttributes().windowAnimations =
            R.style.Theme_MaterialComponents_Dialog
        alertDialog.show()
    }

    @AfterPermissionGranted(RC_CAMERA)
    private fun checkPermissionsAndStartQRScan() {
        val permission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            // Already have permission, do the thing
            openScanner()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.camera_permission_explanation),
                RC_CAMERA, permission
            )
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.pause()
    }

    override fun onResume() {
        super.onResume()
        scannerView.resume()
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required_dialog_title))
            .setMessage(getString(R.string.permission_required_dialog_content))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                checkPermissionsAndStartQRScan()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openScanner()
    }

    private fun openScanner() {
        scannerView.resume()
    }

    fun enableFlash(enable: Boolean) {
        if (enable) {
            scannerView.setTorchOn();
        } else {
            scannerView.setTorchOff()
        }
    }

    companion object {
        const val RC_CAMERA = 1
        const val SCANNER_FRAGMENT_TAG = "Scanner Fragment TAG"
    }
}


//{
//
//    private lateinit var scanViewModel: ScanViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        scanViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(application = activity?.application!!)
//        ).get(ScanViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        scanViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        return root
//    }
//}
