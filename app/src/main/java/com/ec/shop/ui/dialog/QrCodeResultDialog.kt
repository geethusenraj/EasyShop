package com.ec.shop.ui.dialog

import android.app.Dialog
import android.content.Context
import com.ec.shop.R
import kotlinx.android.synthetic.main.layout_qr_result_show.*

class QrCodeResultDialog(var context: Context) {

    private lateinit var dialog: Dialog
    private var onDismissListener: OnDismissListener? = null

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_qr_result_show)
        dialog.setCancelable(false)
        onClicks()
    }

    private fun onClicks() {

        dialog.cancelDialog.setOnClickListener {
            dialog.dismiss()
            onDismissListener?.onDismiss()
        }
        dialog.addDialog.setOnClickListener {
//            it.showSnackBar("Added")
            dialog.dismiss()
            onDismissListener?.onAdd()
            onDismissListener?.onDismiss()
        }
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    fun show(decodedResult: String) {
        dialog.scannedText.text = decodedResult
        dialog.show()
    }


    interface OnDismissListener {
        fun onDismiss()
        fun onAdd();
    }

}

