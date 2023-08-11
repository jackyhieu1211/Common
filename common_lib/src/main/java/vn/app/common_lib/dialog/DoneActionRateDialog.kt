package vn.app.common_lib.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import vn.app.common_lib.R
import vn.app.common_lib.databinding.DialogDoneActionRateBinding
import vn.app.common_lib.extension.click
import vn.app.common_lib.helper.AppPreference
import vn.app.common_lib.utils.ToastUtil

class DoneActionRateDialog : DialogFragment() {

    private val compositeDisposable = CompositeDisposable()
    private val appPreference: AppPreference by inject()

    private var _binding: DialogDoneActionRateBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "DoneActionRateDialog"
        fun newInstance() = DoneActionRateDialog().apply {
            isCancelable = false
        }
    }

    var showRateInAppReview: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = DialogDoneActionRateBinding.inflate(layoutInflater, null, false)
        iniAction()
        return binding.root
    }

    private fun iniAction() {
        compositeDisposable.addAll(
            binding.btnGotoRate.click {
                onRateApp()
            },
            binding.btnCancel.click {
                this@DoneActionRateDialog.dismiss()
            },
            binding.btnNotReally.click {
                onNotReallyApp()
            }
        )
    }

    private fun onNotReallyApp() {
        ToastUtil.showToast(context, "Thank you for your review!")
        dismiss()
    }

    private fun onRateApp() {
        showRateInAppReview?.invoke()
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        appPreference.updateShowRateDelay()
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}