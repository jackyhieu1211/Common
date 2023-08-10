package vn.app.common_lib.extension

import java.io.IOException
import java.text.DecimalFormat

fun Long.formatSize(): String {
	val df = DecimalFormat("0.00")
	val sizeKb = 1024.0f
	val sizeMb = sizeKb * sizeKb
	val sizeGb = sizeMb * sizeKb
	val sizeTerra = sizeGb * sizeKb
	return when {
		this < sizeMb -> df.format(this / sizeKb) + " KB"
		this < sizeGb -> df.format(this / sizeMb) + " MB"
		this < sizeTerra -> df.format(this / sizeGb) + " GB"
		else -> ""
	}
}

fun String.toSize(): Long {
	//601067	/storage/emulated/0/Movies/
	return try {
		val result = (split("/").firstOrNull()?.trim() ?: "0").toLongOrNull().orDefault()
		return result * 1024
	} catch (e: IOException) {
		0L
	}
}

fun Long.isTypeSize(): SizeType {
	val sizeKb = 1024.0f
	val sizeMb = sizeKb * sizeKb
	val sizeGb = sizeMb * sizeKb
	val sizeTerra = sizeGb * sizeKb
	return when {
		this < sizeMb -> SizeType(this / sizeKb, 1)
		this < sizeGb -> SizeType(this / sizeMb, 2)
		this < sizeTerra -> SizeType(this / sizeMb, 3)
		else -> SizeType(this / sizeMb, 3)
	}
}

enum class SizeFile(val rawKey: String, val rawValue: Long) {
	K("K", 1024), M("M", 1024 * 1024), G("F", 1024 * 1024 * 1024)
}

data class SizeType(
	val value: Float,
	val type: Int // =1 is KB, 2 is MB, 3 GB
)

fun Long?.orEmpty(): Long {
	return this ?: 0L
}

fun Double?.orDefault(): Double {
	if (this == null) return 0.0

	return if (this >= 0) this else 0.0
}

fun Float?.orDefault(): Float {
	if (this == null) return 0f

	return if (this >= 0) this else 0f
}


private const val MILLISECONDS = 1000L

private fun Long.convertToSeconds(): Long {
	return this / MILLISECONDS
}

fun Int.getTimeString(): String {
	return if (this.toString().length == 1) {
		"0$this"
	} else this.toString()
}

fun Long.getTimeDiscount(): String {
	val secondsDiscount = this.convertToSeconds().toFloat()
	val mathematical = secondsDiscount / (60 * 60)
	val hour = mathematical.toInt()
	val minus = (60 * (mathematical - hour)).toInt()
	val seconds = (secondsDiscount - (hour * 60 * 60) - (minus * 60)).toInt()

	return "${hour.getTimeString()}:${minus.getTimeString()}:${seconds.getTimeString()}"
}