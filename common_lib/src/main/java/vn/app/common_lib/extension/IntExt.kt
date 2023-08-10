package vn.app.common_lib.extension

import android.content.res.Resources
import android.util.TypedValue

fun Int?.orDefault(): Int {
	return this ?: 0
}

fun Long?.orDefault(): Long {
	return this ?: 0L
}


fun Int.toPx(): Int {
	return this.toFloat().toPx()
}

fun Float.toPx(): Int {
	return TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP,
		this,
		Resources.getSystem().displayMetrics
	).toInt()
}

fun Int?.orDefaultPage(): Int {
	return this ?: 1
}
