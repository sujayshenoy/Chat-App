package com.example.chatapp.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.Utilities
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.databinding.FragmentOtpBinding

class OtpFragment : Fragment(R.layout.fragment_otp) {
    private lateinit var binding: FragmentOtpBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOtpBinding.bind(view)
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        context?.let {
            logger = LoggerImpl("Otp Fragment")
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
        } ?: Log.e("OtpFragment", "Empty Context")

        initClickListeners()
        initObservers()
        initOtpBox()
        animateLoading()
    }

    private fun initObservers() {
        authenticationViewModel.verifyUserStatus.observe(viewLifecycleOwner) {
            dialog.dismiss()
        }

        authenticationViewModel.resendOtpStatus.observe(viewLifecycleOwner) {
            dialog.dismiss()
        }
    }

    private fun animateLoading() {
        val mainText = binding.verificationCodeText
        val subText = binding.verificationCodeSubText
        val codeOneBox = binding.otpEditText1
        val codeTwoBox = binding.otpEditText2
        val codeThreeBox = binding.otpEditText3
        val codeFourBox = binding.otpEditText4
        val codeFiveBox = binding.otpEditText5
        val codeSixBox = binding.otpEditText6
        val verifyOtpButton = binding.verifyOtpButton
        val resendOtpBox = binding.resendOtpBox
        val animDuration = 500L

        mainText.alpha = 0f
        subText.alpha = 0f
        resendOtpBox.alpha = 0f
        verifyOtpButton.alpha = 0f
        codeOneBox.alpha = 0f
        codeTwoBox.alpha = 0f
        codeThreeBox.alpha = 0f
        codeFourBox.alpha = 0f
        codeFiveBox.alpha = 0f
        codeSixBox.alpha = 0f

        mainText.animate().alpha(1f).setDuration(animDuration).start()
        subText.animate().alpha(1f).setDuration(animDuration).start()
        resendOtpBox.animate().alpha(1f).setDuration(animDuration).start()
        codeOneBox.animate().alpha(1f).setDuration(animDuration).start()
        codeTwoBox.animate().alpha(1f).setDuration(animDuration).start()
        codeThreeBox.animate().alpha(1f).setDuration(animDuration).start()
        codeFourBox.animate().alpha(1f).setDuration(animDuration).start()
        codeFiveBox.animate().alpha(1f).setDuration(animDuration).start()
        codeSixBox.animate().alpha(1f).setDuration(animDuration).start()
        verifyOtpButton.animate().alpha(1f).setDuration(animDuration).start()
    }

    private fun initOtpBox() {
        binding.otpEditText1.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            logger.logInfo("Keycode: $keyCode")
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.otpEditText2.requestFocus()
            }
            false
        }

        binding.otpEditText2.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.otpEditText3.requestFocus()
            } else if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isEmpty()) {
                    binding.otpEditText1.setText("")
                }
                binding.otpEditText1.requestFocus()
            }
            false
        }

        binding.otpEditText3.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.otpEditText4.requestFocus()
            } else if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isEmpty()) {
                    binding.otpEditText2.setText("")
                }
                binding.otpEditText2.requestFocus()
            }
            false
        }

        binding.otpEditText4.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.otpEditText5.requestFocus()
            } else if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isEmpty()) {
                    binding.otpEditText3.setText("")
                }
                binding.otpEditText3.requestFocus()
            }
            false
        }

        binding.otpEditText5.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.otpEditText6.requestFocus()
            } else if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isEmpty()) {
                    binding.otpEditText4.setText("")
                }
                binding.otpEditText4.requestFocus()
            }
            false
        }

        binding.otpEditText6.setOnKeyListener { view, keyCode, keyEvent ->
            val text = (view as EditText).text.toString()
            if (keyCode in 7..16 && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isNotEmpty()) {
                    view.setText(keyCode)
                }
                binding.verifyOtpButton.requestFocus()
            } else if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (text.isEmpty()) {
                    binding.otpEditText5.setText("")
                }
                binding.otpEditText5.requestFocus()
            }
            false
        }
    }

    private fun initClickListeners() {
        binding.verifyOtpButton.setOnClickListener {
            dialog.show()
            val otp = getOtp()
            if (otp.isEmpty()) {
                context?.let {
                    Utilities.displayLongToast(it, "Please enter a valid Otp")
                } ?: logger.logError("Empty Context")
                dialog.dismiss()
            } else {
                authenticationViewModel.verifyOtp(otp)
            }
        }

        binding.resendOtpButton.setOnClickListener {
            dialog.show()
            activity?.let { activity ->
                authenticationViewModel.resendOtp(activity)
            } ?: logger.logError("Empty Parent Activity")
        }
    }

    private fun getOtp(): String {
        val code1 = binding.otpEditText1.text.toString()
        val code2 = binding.otpEditText2.text.toString()
        val code3 = binding.otpEditText3.text.toString()
        val code4 = binding.otpEditText4.text.toString()
        val code5 = binding.otpEditText5.text.toString()
        val code6 = binding.otpEditText6.text.toString()

        val otp = "$code1$code2$code3$code4$code5$code6"
        return if (otp.length == 6) otp else ""
    }
}