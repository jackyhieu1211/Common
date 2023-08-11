package vn.app.common_lib.dialog

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import vn.app.common_lib.helper.AppPreference

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    val compositeDisposable = CompositeDisposable()
    val appPreference: AppPreference by inject()

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }
}