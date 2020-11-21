package com.ec.shop.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ec.shop.R
import com.ec.shop.ui.dialog.QrCodeResultDialog
import com.ec.shop.utils.showSnackBar
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import kotlinx.android.synthetic.main.fragment_scan.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ScanFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    ZBarScannerView.ResultHandler {

    private lateinit var beepManager: BeepManager
//    private lateinit var scannerView: DecoratedBarcodeView

    private lateinit var mView: View
    lateinit var scannerView: ZBarScannerView
    private lateinit var alertDialog: AlertDialog
    private lateinit var resultDialog: QrCodeResultDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_scan, container, false)
        return mView.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
//        scannerView = view.findViewById(R.id.QRScannerView)

        val formats = mutableListOf(BarcodeFormat.QR_CODE)
        beepManager = BeepManager(requireActivity())
//        scannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
//        scannerView.setStatusText("")

//        scannerView.decodeContinuous(object : BarcodeCallback {
//            override fun barcodeResult(result: BarcodeResult?) {
//                result?.let {
//                    beepManager.isBeepEnabled = false
//                    beepManager.playBeepSoundAndVibrate()
//
//                    setResultDialog(result.text)
////                    showConfirmationDialog(result.text)
//                }
//            }
//
//            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
//            }
//        })

        checkPermissionsAndStartQRScan()
    }

    private fun initViews() {
        initializeQRCamera()
        setResultDialog()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorTranslucent
            )
        )
        scannerView.setBorderColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryDark
            )
        )
        scannerView.setLaserColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryDark
            )
        )
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        mView.containerScanner.addView(scannerView)
    }

    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }

    private fun setResultDialog() {
        resultDialog = QrCodeResultDialog(requireContext())
        resultDialog.setOnDismissListener(object : QrCodeResultDialog.OnDismissListener {
            override fun onDismiss() {
                resetPreview()
            }
        })
    }

    private fun saveToDataBase(decodedResult: String) {
        resultDialog.show(decodedResult)
        Toast.makeText(context, decodedResult, Toast.LENGTH_SHORT).show()
    }


    private fun showConfirmationDialog(qrData: String?) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_custom_view, null)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create();
        val tvQrData = dialogView.findViewById<TextView>(R.id.qrContent)
//        val btnScanAgain: Button = dialogView.findViewById(R.id.btnScanAgain)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        val btnAdd: Button = dialogView.findViewById(R.id.btnOk)
        tvQrData.text = qrData
//        btnScanAgain.setOnClickListener {
//            alertDialog.cancel()
//        }
        btnAdd.setOnClickListener {
            it.showSnackBar("Added")
        }
        btnCancel.setOnClickListener {
            alertDialog.cancel()
        }
        alertDialog.window!!.attributes.windowAnimations =
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
//        scannerView.pause()
    }

    override fun onResume() {
        super.onResume()
//        scannerView.resume()
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
//        scannerView.resume()
    }

//    fun enableFlash(enable: Boolean) {
//        if (enable) {
//            scannerView.setTorchOn();
//        } else {
//            scannerView.setTorchOff()
//        }
//    }

    companion object {
        const val RC_CAMERA = 1
        const val SCANNER_FRAGMENT_TAG = "Scanner Fragment TAG"
    }

    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty())
            Toast.makeText(context, "Empty Qr Result", Toast.LENGTH_SHORT).show()
        else
            saveToDataBase(contents)
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
