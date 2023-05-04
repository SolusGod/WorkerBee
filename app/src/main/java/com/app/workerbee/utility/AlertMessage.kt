package com.app.workerbee.utility

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.app.workerbee.R

object AlertMessage {

    fun showMessage(mContext: Context, message: String) {
        showMessage(mContext, null, message, mContext.getString(R.string.ok), null, true)
    }

    fun showMessage(mContext: Context, messageResourceId: Int) {
        showMessage(mContext, null, mContext.getString(messageResourceId), mContext.getString(R.string.ok), null, true)
    }

    fun showMessage(mContext: Context, message: String, alertMessageCallback: AlertMessageCallback) {
        showMessage(mContext, null, message, mContext.getString(R.string.ok), alertMessageCallback, true)
    }

    fun showMessage(
        mContext: Context,
        message: String,
        alertMessageCallback: AlertMessageCallback,
        isCancelable: Boolean
    ) {
        showMessage(mContext, null, message, mContext.getString(R.string.ok), alertMessageCallback, isCancelable)
    }


    fun showMessage(
        mContext: Context,
        title: String?,
        message: String,
        positiveButton: String,
        alertMessageCallback: AlertMessageCallback?,
        isCancelable: Boolean
    ) {
        showMessage(mContext, title, message, positiveButton, null, alertMessageCallback, isCancelable)
    }

    fun showMessage(
        mContext: Context,
        title: String?,
        message: String,
        positiveButton: String,
        negativeButton: String?,
        alertMessageCallback: AlertMessageCallback?,
        isCancelable: Boolean
    ) {
        val builder = AlertDialog.Builder(mContext/*, R.style.MyTheme*/)
            .setMessage(message)
            .setPositiveButton(positiveButton) { p0, p1 ->
                alertMessageCallback?.onPositiveButtonClick()
            }
            .setCancelable(isCancelable)

        title.let { builder.setTitle(it) }
        negativeButton.let {
            builder.setNegativeButton(it) { p0, p1 ->
                alertMessageCallback?.onNegativeButtonClick()
            }
        }
        try {
            builder.show()
        } catch (e: Exception) {
           Log.d("error",Utils.checkNull(e.message))
        }
    }
}