package vn.app.common_lib.extension

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<ConstraintLayout>.onStateChanged(action : (newState: Int) -> Unit) {
    addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            action.invoke(newState)
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    })
}