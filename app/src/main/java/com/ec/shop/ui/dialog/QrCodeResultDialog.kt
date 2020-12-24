package com.ec.shop.ui.dialog

import android.app.Dialog
import android.content.Context
import com.ec.shop.R
import com.ec.shop.utils.Utils.generateCustomProductDetails
import kotlinx.android.synthetic.main.layout_qr_result_show.*

class QrCodeResultDialog(var context: Context) {
    private var qrResult = ""
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
            dialog.dismiss()
            onDismissListener?.onAdd(qrResult)
            onDismissListener?.onDismiss()
        }
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    fun show(decodedResult: String) {
        qrResult = decodedResult
        dialog.scannedText.text = generateCustomProductDetails(decodedResult)
        dialog.tvAsk.text = context.getString(R.string.want_to_add)
        dialog.show()
    }


    interface OnDismissListener {
        fun onDismiss()
        fun onAdd(qrResult: String);
    }

}

