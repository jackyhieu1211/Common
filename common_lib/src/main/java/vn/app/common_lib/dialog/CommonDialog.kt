package vn.app.common_lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import vn.app.common_lib.R
import vn.app.common_lib.databinding.CommonDialogBinding
import vn.app.common_lib.extension.click

class CommonDialog : DialogFragment() {

    private var _binding: CommonDialogBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var onOkListener: (() -> Unit)? = null
    var onCancelListener: (() -> Unit)? = null

    companion object {
        const val TAG = "CommonDialog"
        const val ARGS_TITLE = "ARGS_TITLE"
        const val ARGS_MESSAGE = "ARGS_MESSAGE"
        const val ARGS_OK = "ARGS_OK"
        const val ARGS_CANCEL = "ARGS_CANCEL"
        const val ARGS_SHOW_CANCEL = "ARGS_SHOW_CANCEL"

        fun newInstance(
            title: String,
            message: String,
            okText: String,
            canShowCancel: Boolean,
            cancel: String = "Cancel"
        ) =
            CommonDialog().apply {
                arguments = Bundle().apply {
                    putString(ARGS_TITLE, title)
                    putString(ARGS_MESSAGE, message)
                    putString(ARGS_OK, okText)
                    putString(ARGS_CANCEL, cancel)
                    putBoolean(ARGS_SHOW_CANCEL, canShowCancel)
                }
            }
    }

    private val title: String by lazy { arguments?.getString(ARGS_TITLE).orEmpty() }
    private val message: String by lazy { arguments?.getString(ARGS_MESSAGE).orEmpty() }
    private val textOK: String by lazy { arguments?.getString(ARGS_OK).orEmpty() }
    private val cancel: String by lazy { arguments?.getString(ARGS_CANCEL).orEmpty() }
    private val canShowCancel: Boolean by lazy { arguments?.getBoolean(ARGS_SHOW_CANCEL) ?: false }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = CommonDialogBinding.inflate(layoutInflater, null, false)
        initData()
        return binding.root
    }

    private fun initData() {
        compositeDisposable.addAll(
            binding.btnOK.click {
                onOkListener?.invoke()
                dismiss()
            },
            binding.btnCancel.click {
                onCancelListener?.invoke()
                dismiss()
            }
        )
        binding.btnOK.text = textOK
        binding.btnCancel.isInvisible = canShowCancel.not()
        binding.btnCancel.text = cancel
        binding.title.text = title
        binding.message.text = message
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        _binding = null
        super.onDestroy()
    }
}