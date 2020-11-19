package com.ec.shop.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))

}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    isActionEnabled: Boolean = false
) {
    val snackBar = Snackbar.make(this, message, duration)
    if (isActionEnabled) {
        snackBar.action("OK", listener = {
            snackBar.dismiss()
        })
    }
    snackBar.show()
}

fun Snackbar.action(actionRes: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionRes, listener)
    color?.let { setActionTextColor(color) }
}

fun <T> Context.openActivity(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)

}