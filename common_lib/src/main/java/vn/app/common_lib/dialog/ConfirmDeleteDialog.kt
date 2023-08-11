package vn.app.common_lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vn.app.common_lib.R
import vn.app.common_lib.databinding.DialogConfirmDeleteBinding
import vn.app.common_lib.extension.click


class ConfirmDeleteDialog : DialogFragment() {
    private lateinit var binding: DialogConfirmDeleteBinding

    companion object {
        const val TAG = "ConfirmDeleteDialog"
        const val TXT_MESSAGE = "TXT_MESSAGE"
        fun getInstance(message: String) =
            ConfirmDeleteDialog().apply {
                arguments = Bundle().apply {
                    putString(TXT_MESSAGE, message)
                }
            }
    }

    var onDeleteOkClick: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DialogConfirmDeleteBinding.inflate(layoutInflater, null, false)
        initData()
        setupListener()
        return binding.root
    }

    private fun initData() {
        val message = arguments?.getString(TXT_MESSAGE)
        message?.let {
            binding.lblDeleteMessage.text = it
        }
    }

    private fun setupListener() {
        binding.btnCancel.click {
            dismiss()
        }
        binding.btnDelete.click {
            onDeleteOkClick?.invoke()
            dismiss()
        }
    }
}