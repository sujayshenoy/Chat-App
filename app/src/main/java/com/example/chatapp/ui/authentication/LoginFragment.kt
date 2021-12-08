package com.example.chatapp.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        context?.let {
            logger = LoggerImpl("Login Fragment")
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
        } ?: Log.e("LoginFragment", "Empty Context")

        initClickListeners()
        initObservers()
        animateLoading()
    }

    private fun initObservers() {
        authenticationViewModel.sendOtpStatus.observe(viewLifecycleOwner) {
            dialog.dismiss()
        }

        authenticationViewModel.verifyUserStatus.observe(viewLifecycleOwner) {
            dialog.dismiss()
        }
    }

    private fun initClickListeners() {
        binding.sendButton.setOnClickListener {
            dialog.show()
            val countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
            val phone = binding.phoneInputEditText.text.toString()
            val fullPhone = "$countryCode$phone"
            val validationResult: Boolean = validate(phone)
            if (validationResult) {
                authenticationViewModel.sendOtp(
                    requireActivity(),
                    fullPhone
                )
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun validate(phone: String): Boolean {
        return if (phone.matches("[0-9]{10}".toRegex())) {
            true
        } else {
            binding.phoneInputEditText.error = getString(R.string.invalid_phone_error)
            false
        }
    }

    private fun animateLoading() {
        val logoIcon = binding.logo
        val welcomeText = binding.loginWelcomeText
        val phoneBox = binding.phoneInputBox
        val sendButton = binding.sendButton
        val tncText = binding.tncText
        val fromSujay = binding.fromSujayText
        val animationDuration = 200L

        welcomeText.alpha = 0f
        tncText.alpha = 0f
        fromSujay.alpha = 0f
        logoIcon.scaleX = 0f
        logoIcon.scaleY = 0f
        phoneBox.scaleX = 0f
        phoneBox.scaleY = 0f
        sendButton.scaleX = 0f
        sendButton.scaleY = 0f

        welcomeText.animate().alpha(1f).setDuration(animationDuration).withEndAction {
            logoIcon.animate().scaleX(1f).scaleY(1f).setDuration(animationDuration).start()
        }.start()

        phoneBox.animate().scaleX(1f).scaleY(1f).setDuration(animationDuration)
            .withEndAction {
                sendButton.animate().scaleX(1f).scaleY(1f)
                    .setDuration(animationDuration).start()
            }.start()

        tncText.animate().alpha(1f).setDuration(animationDuration).start()
        fromSujay.animate().alpha(1f).setDuration(animationDuration).start()
    }
}