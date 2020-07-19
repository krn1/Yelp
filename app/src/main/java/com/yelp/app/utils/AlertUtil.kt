package com.yelp.app.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object AlertUtil {
    private var toast: Toast? = null

    fun toastUser(context: Context?, message: String?) {
        if (message == null || message.isEmpty()) {
            return
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast!!.setGravity(Gravity.BOTTOM, 0, 200)
        toast!!.show()
    }
}
