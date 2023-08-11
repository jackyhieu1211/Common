package vn.app.common_lib.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vn.app.common_lib.R
import vn.app.common_lib.databinding.DialogJumpToPageBinding
import vn.app.common_lib.extension.click
import vn.app.common_lib.extension.orDefaultPage
import vn.app.common_lib.extension.postDelay
import vn.app.common_lib.extension.showKeyboard
import vn.app.common_lib.utils.ToastUtil


class JumpToPageDialog : DialogFragment() {
    private lateinit var binding: DialogJumpToPageBinding

    companion object {
        const val TAG = "JumpToPageDialog"
        private const val EXTRA_TOTAL_PAGE = "EXTRA_TOTAL_PAGE"
        fun getInstance(totalPage: Int) =
            JumpToPageDialog().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_TOTAL_PAGE, totalPage)
                }
            }
    }

    var onJumpPage: ((Int) -> Unit)? = null

    private val totalPage: Int by lazy { arguments?.getInt(EXTRA_TOTAL_PAGE).orDefaultPage() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.Transparent)
        builder.setView(setupView())
        return builder.create()
    }

    private fun setupView(): View? {
        val activity = activity ?: return null
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DialogJumpToPageBinding.inflate(layoutInflater, null, false)
        initView()
        setupListener()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.txtJumToPage.hint = "1 - $totalPage"
        postDelay(duration = 300L) {
            onShowKeyBoard()
        }

    }

    private fun onShowKeyBoard() {
        if (isAdded && context != null) {
            binding.txtJumToPage.showKeyboard()
        }
    }

    private fun setupListener() {
        binding.btnCancel.click {
            dismiss()
        }

        binding.btnOK.click {
            handleJumpToPage()
        }
    }

    private fun handleJumpToPage() {
        val page = binding.txtJumToPage.text?.trim().toString()
        val pageJumTo = page.toIntOrNull().orDefaultPage().minus(1)
        when {
            pageJumTo < 0 -> ToastUtil.showToast(context, "Greater than or equal 1")
            pageJumTo >= totalPage -> ToastUtil.showToast(context, "Beyond totality")
            else -> onJumpPage?.invoke(pageJumTo)
        }

        dismiss()
    }

}