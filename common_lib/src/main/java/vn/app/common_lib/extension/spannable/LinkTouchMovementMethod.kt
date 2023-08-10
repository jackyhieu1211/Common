package vn.app.common_lib.extension.spannable

import android.text.Selection
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

class LinkTouchMovementMethod : LinkMovementMethod() {
    private var pressedSpan: ClickableStateSpan? = null

    override fun onTouchEvent(
        textView: TextView, spannable: Spannable, event: MotionEvent
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressedSpan = getPressedSpan(textView, spannable, event)
                if (pressedSpan != null) {
                    pressedSpan?.setPressed(true)
                    Selection.setSelection(
                        spannable, spannable.getSpanStart(pressedSpan),
                        spannable.getSpanEnd(pressedSpan)
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val touchedSpan = getPressedSpan(textView, spannable, event)
                if (pressedSpan != null && touchedSpan !== pressedSpan) {
                    pressedSpan?.setPressed(false)
                    pressedSpan = null
                    Selection.removeSelection(spannable)
                }
            }
            else -> {
                if (pressedSpan != null) {
                    pressedSpan!!.setPressed(false)
                    super.onTouchEvent(textView, spannable, event)
                }
                pressedSpan = null
                Selection.removeSelection(spannable)
            }
        }
        return true
    }

    private fun getPressedSpan(
        textView: TextView, spannable: Spannable, event: MotionEvent
    ): ClickableStateSpan? {
        val x = event.x.toInt() - textView.totalPaddingLeft + textView.scrollX
        val y = event.y.toInt() - textView.totalPaddingTop + textView.scrollY

        val layout = textView.layout
        val position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x.toFloat())

        val links = spannable.getSpans(position, position, ClickableStateSpan::class.java)
        var clickedSpan: ClickableStateSpan? = null
        if (links.isNotEmpty() && positionWithinTag(position, spannable, links[0])) {
            clickedSpan = links[0]
        }

        return clickedSpan
    }

    private fun positionWithinTag(position: Int, spannable: Spannable, tag: Any): Boolean {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag)
    }
}

class ClickableStateSpan(
    @ColorInt private val normalTextColor: Int,
    @ColorInt private val pressedTextColor: Int = 0,
    private val clickListener: ((View) -> Unit)? = null
) : ClickableSpan() {
    private var isPressed: Boolean = false

    fun setPressed(isSelected: Boolean) {
        isPressed = isSelected
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = if (isPressed && pressedTextColor != 0) pressedTextColor else normalTextColor
        ds.isUnderlineText = false
    }

    override fun onClick(view: View) {
        clickListener?.invoke(view)
    }
}

class OnlyClickableSpan(private val clickListener: (View) -> Unit) : ClickableSpan() {

    override fun onClick(view: View) = clickListener(view)

    override fun updateDrawState(ds: TextPaint) {}
}