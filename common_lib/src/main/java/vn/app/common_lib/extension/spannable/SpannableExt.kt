package vn.app.common_lib.extension.spannable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.ParcelableSpan
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import vn.app.common_lib.R
import kotlin.math.roundToInt

fun TextView.setLinkSpanText(spanText: CharSequence) {
    movementMethod = LinkTouchMovementMethod()
    highlightColor = Color.TRANSPARENT
    text = spanText
}

fun SpannableStringBuilder.appendSpace(): SpannableStringBuilder = append(" ")

fun SpannableStringBuilder.append(texts: List<CharSequence>): SpannableStringBuilder {
    texts.forEach {
        append(it)
    }
    return this
}

fun SpannableStringBuilder.withClickLinkSpan(
    context: Context, text: CharSequence, click: () -> Unit
): SpannableStringBuilder {
    return withClickStateSpan(
        text = text,
        textColor = context.getColor(R.color.white),
        pressTextColor = context.getColor(R.color.black),
        otherStyles = context.styleSpans { underline() },
        click = { click() }
    )
}

/*
* Only use with LinkTouchMovementMethod
* */
fun SpannableStringBuilder.withClickStateSpan(
    text: CharSequence,
    @ColorInt textColor: Int,
    @ColorInt pressTextColor: Int = 0,
    otherStyles: StyleSpans? = null,
    click: (View) -> Unit
): SpannableStringBuilder {
    val from = length
    append(text)

    val clickSpan = ClickableStateSpan(textColor, pressTextColor, click)
    setSpan(clickSpan, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    otherStyles?.forEach {
        setSpan(it, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}

fun SpannableStringBuilder.withClickSpan(
    text: CharSequence,
    styles: StyleSpans,
    click: (View) -> Unit
): SpannableStringBuilder {
    val from = length
    append(text)

    val clickSpan = OnlyClickableSpan(click)
    setSpan(clickSpan, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    styles.forEach {
        setSpan(it, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}

fun SpannableStringBuilder.withStyleSpan(
    text: CharSequence,
    style: StyleSpans
): SpannableStringBuilder {
    val from = length
    append(text)

    style.forEach {
        setSpan(it, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}

fun Context.styleSpans(options: StyleSpans.() -> Unit) = StyleSpans(this).apply(options)

class StyleSpans(private val context: Context) : Iterable<Any> {
    private val spans = mutableListOf<Any>()

    override fun iterator() = spans.iterator()

    fun size(@DimenRes id: Int) =
        spans.add(AbsoluteSizeSpan(context.resources.getDimensionPixelSize(id)))

    fun color(@ColorRes id: Int) =
        spans.add(ForegroundColorSpan(ContextCompat.getColor(context, id)))

    fun typeface(style: Int) = spans.add(StyleSpan(style))

    fun fontFamily(family: String) = spans.add(TypefaceSpan(family))
    fun sansSerifMedium() = fontFamily("sans-serif-medium")
    fun sansSerifRegular() = fontFamily("sans-serif-regular")

    fun textAppearance(@StyleRes appearanceId: Int) =
        spans.add(TextAppearanceSpan(context, appearanceId))

    fun backgroundColor(@ColorRes id: Int) =
        spans.add(BackgroundColorSpan(ContextCompat.getColor(context, id)))

    fun underline() = spans.add(UnderlineSpan())

    fun strikeThrough(@Px thickness: Float) = spans.add(CustomStrikeThroughSpan(thickness))

    fun custom(span: ParcelableSpan) = spans.add(span)

//    fun icon(@DrawableRes id: Int, size: Int): ImageSpan? {
//        val drawable = ContextCompat.getDrawable(context, id)?.apply {
//            setBounds(0, 0, size, size)
//        }
//        return ImageSpan(drawable ?: return null)
//    }

}

/*
* This StrikeThroughSpan is only applied for one line span
* */
class CustomStrikeThroughSpan(@Px private val thickness: Float) :
    ReplacementSpan() {
    private val strikeThruPaint: Paint = Paint()

    init {
        strikeThruPaint.strokeWidth = thickness
    }

    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).roundToInt()
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        // Draw text with all prev styles (spans)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)

        // Draw strikethru
        val width = getSize(paint, text, start, end, null)
        val yPos = (bottom - top).toFloat() / 2
        strikeThruPaint.color = paint.color
        canvas.drawLine(x, yPos, x + width.toFloat(), yPos, strikeThruPaint)
    }
}