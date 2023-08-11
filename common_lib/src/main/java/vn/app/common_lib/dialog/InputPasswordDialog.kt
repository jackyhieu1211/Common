package vn.app.common_lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import vn.app.common_lib.R
import vn.app.common_lib.databinding.LayoutInputPasswordBinding
import vn.app.common_lib.extension.click
import vn.app.common_lib.extension.validateAll

class InputPasswordDialog : DialogFragment() {

    companion object {
        const val TAG = "InputPasswordDialog"
        private const val ARGS_FILE_NAME = "ARGS_FILE_NAME"
        private const val ARGS_MESSAGE = "ARGS_MESSAGE"
        fun getInstance(fileName: String, message: String) =
            InputPasswordDialog().apply {
                arguments = Bundle().apply {
                    putString(ARGS_FILE_NAME, fileName)
                    putString(ARGS_MESSAGE, message)
                }
            }
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var _binding: LayoutInputPasswordBinding? = null
    private val binding get() = _binding!!

    var onInputListener: ((String) -> Unit)? = null
    var cancelListener: (() -> Unit)? = null
    private val fileName: String by lazy { arguments?.getString(ARGS_FILE_NAME).orEmpty() }
    private val message: String by lazy { arguments?.getString(ARGS_MESSAGE).orEmpty() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = LayoutInputPasswordBinding.inflate(layoutInflater, null, false)
        compositeDisposable.addAll(
            arrayOf(binding.edtPassword.textChanges().map {
                it.isNotBlank()
            }).validateAll { isAllValidated ->
                binding.btnInputPassword.isEnabled = isAllValidated
            }
        )
        binding.message.text = message
        binding.tvTitleFileName.text = fileName

        binding.btnCancel.click {
            dialog?.dismiss()
            cancelListener?.invoke()
        }

        binding.btnInputPassword.click {
            val password = binding.edtPassword.text.toString().trim()
            dialog?.dismiss()
            onInputListener?.invoke(password)
        }
        return binding.root
    }

    fun showMessageError() {
        binding.message.isVisible = true
    }

    fun hideMessageError() {
        binding.message.isVisible = false
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}