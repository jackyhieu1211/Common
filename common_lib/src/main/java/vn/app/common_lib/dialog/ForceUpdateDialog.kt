package vn.app.common_lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import vn.app.common_lib.R
import vn.app.common_lib.databinding.ForceUpdateDialogBinding
import vn.app.common_lib.extension.click

class ForceUpdateDialog : DialogFragment() {

    private var _binding: ForceUpdateDialogBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var onOkListener: (() -> Unit)? = null

    companion object {
        const val TAG = "ForceUpdateDialog"

        fun newInstance() =
            ForceUpdateDialog().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = ForceUpdateDialogBinding.inflate(layoutInflater, null, false)
        initData()
        return binding.root
    }

    private fun initData() {
        compositeDisposable.addAll(
            binding.btnOK.click {
                dismiss()
                onOkListener?.invoke()
            }
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        _binding = null
        super.onDestroy()
    }
}