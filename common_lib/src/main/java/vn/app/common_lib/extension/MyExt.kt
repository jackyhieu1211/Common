package vn.app.common_lib.extension

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

fun Activity.postDelay(duration : Long = 500L, action: () -> Unit) {
	Handler(Looper.getMainLooper()).postDelayed({
		action.invoke()
	}, duration)
}

fun Fragment.postDelay(duration : Long = 500L,action: () -> Unit) {
	Handler(Looper.getMainLooper()).postDelayed({
		action.invoke()
	}, duration)
}

fun Boolean?.orDefault(): Boolean {
	return this ?: false
}