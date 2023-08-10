package vn.app.common_lib.extension

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.MenuItem

fun MenuItem.setTitleColor(color: Int) {
    val hexColor = Integer.toHexString(color).uppercase().substring(2)
    val html = "<font color='#$hexColor'>$title</font>"
    this.title = html.parseAsHtml()
}


@SuppressLint("ObsoleteSdkInt")
@Suppress("DEPRECATION")
fun String.parseAsHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}