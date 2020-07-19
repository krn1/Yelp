package com.yelp.app.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.yelp.R

object AlertUtil {
    private var toast: Toast? = null

    fun displayError(context: Activity?, errorMsg: String?) {
        var errorMsg = errorMsg
        if (context == null || context.isFinishing) {
            //no-op
            return
        }
        if (errorMsg == null || errorMsg.isEmpty() || errorMsg.equals(
                "unavailable",
                ignoreCase = true
            )
        ) {
            errorMsg = context.getString(R.string.network_error)
        }
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setMessage(errorMsg)
            .setPositiveButton("Okay"
            ) { arg0, arg1 ->
                // do nothing
            }
        alertDialogBuilder.create().show()
    }

    fun toastUser(context: Context?, message: String?) {
        if (message == null || message.isEmpty()) {
            return
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.setGravity(Gravity.BOTTOM, 0, 200)
        toast!!.show()
    }

    fun dismissToast(context: Context?) {
        if (toast != null) {
            toast!!.cancel()
        }
    }
}
