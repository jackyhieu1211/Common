package vn.app.common_lib.extension

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import vn.app.common_lib.R
import vn.app.common_lib.utils.AnimatorUtils
import java.util.concurrent.TimeUnit

private const val DURATION_DELAY = 500L

fun View.getBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
    //return drawToBitmap(Bitmap.Config.ARGB_8888)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.click(duration: Long = DURATION_DELAY, subscribeListener: () -> Unit): Disposable {
    return clicks().throttleFirst(duration, TimeUnit.MILLISECONDS).subscribe(
        {
            subscribeListener()
        },
        {
            Timber.e(it)
        }
    )
}

fun View.hideKeyBoard() {
    val imm = context.applicationContext.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

//fun View.showKeyBoard() {
//    val imm = this.context.applicationContext.getSystemService(
//        Context.INPUT_METHOD_SERVICE
//    ) as InputMethodManager
//    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//}

fun EditText.showKeyboard(context: Context) {
    requestFocus()
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun View.setHideKeyboardOnTouchOutside(
    excludeViewIds: List<Int> = mutableListOf(),
    nextFocus: View? = null,
    onHide: (() -> Unit)? = null,
) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (this !is EditText && this.id.isOneOf(excludeViewIds).not()) {
        setOnTouchListener { _, _ ->
            hideKeyBoard()
            nextFocus?.requestFocus()
            onHide?.invoke()
            this.clearFocus()
            false
        }
    }
    // If a layout container, iterate over children and seed recursion.
    if (this is ViewGroup) {
        forEach {
            it.setHideKeyboardOnTouchOutside(excludeViewIds, nextFocus, onHide)
        }
    }
}

fun Int.isOneOf(list: List<Int>): Boolean = list.contains(this)

fun View.updateMargin(
    @Px start: Int? = null,
    @Px top: Int? = null,
    @Px end: Int? = null,
    @Px bottom: Int? = null,
) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        // updateMargins(left = newMarginStart)
        updateMarginsRelative(
            start = start ?: marginStart,
            top = top ?: topMargin,
            end = end ?: marginEnd,
            bottom = bottom ?: bottomMargin
        )
    }
}


fun TextView.showDrawableStoragePath(show: Boolean, startResId: Int) {
    if (show) {
        setCompoundDrawablesWithIntrinsicBounds(startResId, 0, R.drawable.icon_arrow_next, 0)
    } else {
        setCompoundDrawablesWithIntrinsicBounds(startResId, 0, 0, 0)
    }
}


fun TextView.setDrawableStart(resId: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0)
}

fun TextView.setDrawableEnd(resId: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, resId, 0)
}

fun TextView.setDrawableStartEnd(startResId: Int, endResId: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(startResId, 0, endResId, 0)
}

fun ExtendedFloatingActionButton.rotateFabForward() {
    ViewCompat.animate(this)
        .rotation(45.0f)
        .withLayer()
        .setDuration(200L)
        .setInterpolator(OvershootInterpolator(10.0f))
        .start()
}

fun ExtendedFloatingActionButton.rotateFabBackward() {
    ViewCompat.animate(this)
        .rotation(0.0f)
        .withLayer()
        .setDuration(0L)
        .setInterpolator(OvershootInterpolator(10.0f))
        .start()
}

/**
 * Shows dialog fragment which created by [dialogFragmentFactory] if the fragment [tag] isn't on
 * the backstack yet. This ensure only one dialog display at the same time.
 */
fun FragmentManager.showOnce(tag: String, dialogFragmentFactory: () -> DialogFragment) {
    val isShowing = findFragmentByTag(tag) != null
    if (isShowing) {
        return
    }
    val dialog = dialogFragmentFactory()
    try {
        dialog.show(this, tag)
    } catch (e: IllegalStateException) {
        val ft = beginTransaction()
        ft.add(dialog, tag)
        ft.commitAllowingStateLoss()
    }
}


/**
 * Run hint View.
 */
fun View.hintViewVerticalBottom(onDone: (() -> Unit)? = null) {
    if (visibility != View.GONE) {
        val y = (height + height / 5).toFloat()
        val animator1 = ObjectAnimator.ofFloat(this, AnimatorUtils.TRANSLATION_Y, y)
        // val animator1 = ObjectAnimator.ofFloat<View>(this, AnimatorUtils.TRANSLATION_Y, y)
        animator1.repeatCount = 0
        animator1.duration = 300
        val set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
                onDone?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.play(animator1)
        set.start()
    }
}

/**
 * Run show view.
 */
fun View.runViewVertical(onDone: (() -> Unit)? = null) {
    if (visibility != View.VISIBLE) {
        val animator2 = ObjectAnimator.ofFloat(this, AnimatorUtils.TRANSLATION_Y, 0f)
        animator2.repeatCount = 0
        animator2.duration = 300
        val set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                onDone?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.play(animator2)
        set.start()
    }
}

fun View.tryShowKeyboard() {
    (getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


/**
 * Run show view.
 */
fun View.hintViewVerticalTop(onDone: (() -> Unit)? = null) {
    if (visibility != View.GONE) {
        val y = (height + height / 5).toFloat()
        val animator1 = ObjectAnimator.ofFloat(this, AnimatorUtils.TRANSLATION_Y, -y)
        // val animator1 = ObjectAnimator.ofFloat<View>(this, AnimatorUtils.TRANSLATION_Y, y)
        animator1.repeatCount = 0
        animator1.duration = 300
        val set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
                onDone?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.play(animator1)
        set.start()
    }
}
