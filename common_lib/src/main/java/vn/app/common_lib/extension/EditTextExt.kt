package vn.app.common_lib.extension

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


@SuppressLint("ClickableViewAccessibility")
fun EditText.drawableEndClickListener(listener: () -> Unit) {
    setOnTouchListener(object : OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val drawable = compoundDrawables.getOrNull(2) ?: return false
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - drawable.bounds.width())) {
                    listener()
                    return true
                }
            }
            return false
        }
    })
}

fun EditText.onSearch(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun EditText.setOnEditorDone(
    condition: () -> Boolean = { text.isNotBlank() },
    onDone: (String) -> Unit
) {
    setOnEditorActionListener { _, actionId, keyEvent ->
        if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
            if (condition()) {
                onDone(text.toString().trim())
                return@setOnEditorActionListener true
            }
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.disableClipboard() {
//	isLongClickable = false
//	setTextIsSelectable(false)
    customSelectionActionModeCallback = object : android.view.ActionMode.Callback {
        override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {

            return false
        }

        override fun onDestroyActionMode(mode: android.view.ActionMode?) {

        }

        override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(
            mode: android.view.ActionMode?,
            item: MenuItem?
        ): Boolean {
            return false
        }
    }
}


fun EditText.setOnTouchDrawableListener(
    position: DrawablePosition,
    action: () -> Unit
) {
    setOnTouchListener(object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val drawable = compoundDrawables.getOrNull(position.value) ?: return false
            val padding = when (position) {
                DrawablePosition.LEFT -> paddingStart
                DrawablePosition.RIGHT -> paddingEnd
                DrawablePosition.TOP -> paddingTop
                DrawablePosition.BOTTOM -> paddingBottom
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX + padding >= (right - drawable.bounds.width())) {
                    action()
                    return true
                }
            }
            return false
        }
    })
}

enum class DrawablePosition(val value: Int) {
    LEFT(0),
    TOP(1),
    RIGHT(2),
    BOTTOM(3),

}


@SuppressLint("ClickableViewAccessibility")
fun TextView.drawableEndClickListener(listener: () -> Unit) {
    setOnTouchListener(object : OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val drawable = compoundDrawables.getOrNull(2) ?: return false
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - drawable.bounds.width())) {
                    listener()
                    return true
                }
            }
            return false
        }
    })
}

fun EditText.setDrawableEnd(resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0)
}

fun EditText.hideAndShowPassword() {
    transformationMethod = if (transformationMethod == PasswordTransformationMethod.getInstance()) {
        HideReturnsTransformationMethod.getInstance()
    } else {
        PasswordTransformationMethod.getInstance()
    }
    setSelection(text.length)
}

fun EditText.limitLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}

fun EditText.showKeyboard() {
    if (requestFocus()) {
        (getActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, SHOW_IMPLICIT)
        setSelection(text.length)
    }
}

fun View.getActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}


fun EditText?.toPageOrDefault(): Int {
    val page = this?.text?.trim().toString().toIntOrNull() ?: 1
    return if (page > 0) page else 1
}

