package com.ec.shop.ui.scan

import android.app.Application
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ec.shop.R
import com.ec.shop.constants.Constants.Companion.SHOW_DIALOG
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class ScanViewModel(private val application: Application) : ViewModel(),
    ZBarScannerView.ResultHandler {


    private lateinit var scannerView: ZBarScannerView

    private val _scannerViewItem = MutableLiveData<ZBarScannerView>()
    val scannerViewItem: LiveData<ZBarScannerView>
        get() = _scannerViewItem

    private val _showQRDialog = MutableLiveData<Any>()
    val showQRDialog: LiveData<Any>
        get() = _showQRDialog


    fun startQRCamera() {
        scannerView.startCamera()
    }

    fun initViews() {
        initializeQRCamera()
        _showQRDialog.value = SHOW_DIALOG
    }


    fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(application.applicationContext)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(
            ContextCompat.getColor(
                application.applicationContext,
                R.color.colorTranslucent
            )
        )
        scannerView.setBorderColor(
            ContextCompat.getColor(
                application.applicationContext,
                R.color.colorPrimaryDark
            )
        )
        scannerView.setLaserColor(
            ContextCompat.getColor(
                application.applicationContext,
                R.color.colorPrimaryDark
            )
        )
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        _scannerViewItem.value = scannerView
    }

    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty())
            Toast.makeText(
                application.applicationContext,
                R.string.empty_result, Toast.LENGTH_SHORT
            )
                .show()
        else
            saveToDataBase(contents)
    }

    private fun saveToDataBase(contents: String) {
        _showQRDialog.value = contents
    }
}