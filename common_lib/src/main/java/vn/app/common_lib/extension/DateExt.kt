package vn.app.common_lib.extension

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*

//timeMs: Long
fun Long.formatVideoTime(): String {
    if (this <= 0 || this >= 24 * 60 * 60 * 1000) {
        return "00:00"
    }
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    val stringBuilder = StringBuilder()
    val formatter = Formatter(stringBuilder, Locale.getDefault())
    return if (hours > 0) {
        formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        formatter.format("%02d:%02d", minutes, seconds).toString()
    }
}

fun Long.formatFileDate(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return sdf.format(Date(this * 1000))
}

fun Long.formatDateForStorageFile(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatTime(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDateTime(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDateTimeExtract(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDateTimeHourExtract(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDataAnnot(): String {
    if (this == 0L) return ""
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(this))
}

fun getCurrentDateTime(): String {
    return "(${System.currentTimeMillis().formatDateTime()})"
}


fun DateTime.toTimeAgoRecent(): String {
    val now = DateTime.now()
    val yesterday = now.minusDays(1)
    return if (this.year == now.year && this.monthOfYear == now.monthOfYear && this.dayOfMonth == now.dayOfMonth) {
        "Today"
    } else if (this.year == yesterday.year && this.monthOfYear == yesterday.monthOfYear && this.dayOfMonth == yesterday.dayOfMonth) {
        "Yesterday"
    } else {
        val format = DateTimeFormat.forPattern("dd/MM/yyyy")
        format.print(this)
    }
}