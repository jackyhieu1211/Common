package vn.app.common_lib.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.app.common_lib.R
import vn.app.common_lib.databinding.BottomsheetDialogFeedbackBinding
import vn.app.common_lib.extension.click
import vn.app.common_lib.utils.ToastUtil

class FeedbackBottomSheetDialog : BaseBottomSheetDialogFragment() {

    private var _binding: BottomsheetDialogFeedbackBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "FeedbackBottomSheetDialog"
        fun newInstance() = FeedbackBottomSheetDialog().apply {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        iniAction()
    }

    private fun initView() {

    }

    private fun iniAction() {
        compositeDisposable.add(
            binding.btnFeedback.click {
                onFeedback()
            }
        )
    }

    private fun onFeedback() {
        val message = binding.txtFeedback.text
        if (message.isNullOrEmpty()) {
            ToastUtil.showToast(context, R.string.error_feedback_empty)
            return
        }

        val email = "support@gmail.com"
        val appName = "Feedback"
        val extraText = "Info: API-${android.os.Build.VERSION.SDK_INT} MODEL-${android.os.Build.MODEL} \n $message"

        startActivity(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "$appName feedback!")
            putExtra(Intent.EXTRA_TEXT, extraText)
        })
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}