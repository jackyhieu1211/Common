package vn.app.common_lib.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar


object ToastUtil {

    private var toast: Toast? = null

    fun showToast(context: Context?, msg: String) {
        val nonNullContext = context ?: return
        toast?.cancel()
        toast = Toast.makeText(nonNullContext.applicationContext, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun showToast(context: Context?, @StringRes msgResId: Int) {
        val nonNullContext = context ?: return
        toast?.cancel()
        toast = Toast.makeText(nonNullContext.applicationContext, msgResId, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun showToast(view: View, message: String) {
        val snackBar = Snackbar
            .make(view, message, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }
}