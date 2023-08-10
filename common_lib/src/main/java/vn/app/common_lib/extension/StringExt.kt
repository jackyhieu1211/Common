package vn.app.common_lib.extension

import android.net.Uri
import java.util.*

fun Int.isNone(): Boolean {
    return this == 0
}

fun String.getUniqueLongFromString(): Long {
    return UUID.nameUUIDFromBytes(this.toByteArray()).mostSignificantBits
}

fun Uri.toName(): String {
    return this.lastPathSegment?.split(".")?.firstOrNull() ?: "PDF_1102"
}