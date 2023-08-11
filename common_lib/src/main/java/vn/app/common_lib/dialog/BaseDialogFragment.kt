package vn.app.common_lib.dialog

import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import vn.app.common_lib.helper.AppPreference

open class BaseDialogFragment: DialogFragment() {
    val compositeDisposable = CompositeDisposable()
    val appPreference : AppPreference by inject()

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }
}