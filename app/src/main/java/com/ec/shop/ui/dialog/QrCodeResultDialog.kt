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
            dialog.dismiss()
            onDismissListener?.onAdd()
            onDismissListener?.onDismiss()
        }
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    fun show(decodedResult: String) {
        if (decodedResult.contains("*")) {
            val textSplit = decodedResult.split("*")
            val customText = textSplit[1] + " =  Rs." + textSplit[2] + "-/-"
            dialog.scannedText.text = customText
        }
        dialog.tvAsk.text = context.getString(R.string.want_to_add)
        dialog.show()
    }


    interface OnDismissListener {
        fun onDismiss()
        fun onAdd();
    }

}

