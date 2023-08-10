package vn.app.common_lib.extension

import android.text.SpannableStringBuilder
import android.widget.TextView
import vn.app.common_lib.R
import vn.app.common_lib.extension.spannable.styleSpans
import vn.app.common_lib.extension.spannable.withStyleSpan

fun TextView.setTextOutputFileName(fileName: String, extension: String) {
    val spannable = SpannableStringBuilder()
        .withStyleSpan(fileName, context.styleSpans {
            color(R.color.colorTextView)
            size(R.dimen.text_size_14sp)
        })
        .append("_")
        .withStyleSpan(extension, context.styleSpans {
            color(R.color.colorTextView)
            size(R.dimen.text_size_14sp)
        })
        .withStyleSpan(".pdf", context.styleSpans {
            color(R.color.colorTextView)
            size(R.dimen.text_size_14sp)
        })
    text = spannable
}

fun TextView.setOneTimePurchase(price: String) {
    val spannable = SpannableStringBuilder()
        .withStyleSpan("ONE TIME PURCHASE", context.styleSpans {
            size(R.dimen.text_size_14sp)
        })
        .append("\n")
        .withStyleSpan("Only $price", context.styleSpans {
            size(R.dimen.text_size_10sp)
        })
    text = spannable
}

fun TextView.setSaleOffPurchase(price: String) {
    val spannable = SpannableStringBuilder()
        .withStyleSpan("ONE TIME PURCHASE", context.styleSpans {
            size(R.dimen.text_size_14sp)
        })
        .append("\n")
        .withStyleSpan("Only $price (Best offer)", context.styleSpans {
            size(R.dimen.text_size_10sp)
        })
    text = spannable
}
