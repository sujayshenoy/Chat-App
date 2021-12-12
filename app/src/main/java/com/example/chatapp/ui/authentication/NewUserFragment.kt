package com.example.chatapp.ui.authentication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.MESSAGE_TOKEN
import com.example.chatapp.common.sharedpreferences.SharedPrefUtilImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.FragmentNewUserBinding

class NewUserFragment : Fragment(R.layout.fragment_new_user) {
    private lateinit var binding: FragmentNewUserBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private val logger: Logger = LoggerImpl("New User Fragment")
    private lateinit var sharedPref: SharedPrefUtil
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewUserBinding.bind(view)
        context?.let {
            sharedPref = SharedPrefUtilImpl(it)
        } ?: logger.logError("Empty Context")
        activity?.let {
            authenticationViewModel = ViewModelProvider(it)[AuthenticationViewModel::class.java]
        } ?: logger.logError("Empty parent Activity")

        getFromArgs()
        binding.welcomeFirstText.text = "Hello,\n${user.phone}"
        initClickListeners()
        animateLoading()
    }

    private fun getFromArgs() {
        arguments?.let {
            user = it.getSerializable(ARG_USER) as User
        }
    }

    private fun initClickListeners() {
        binding.nextButton.setOnClickListener {
            it as ImageView
            val animDuration = 200L
            it.animate().scaleX(0.7f).scaleY(0.7f).setDuration(animDuration).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(animDuration).withEndAction {
                    var userName = binding.userNameTextEdit.text.toString()
                    user.name = if (userName.isEmpty()) user.phone else userName
                    authenticationViewModel.addUserToDb(
                        user.copy(
                            messageToken = context?.let { context ->
                                SharedPrefUtilImpl(
                                    context
                                ).getString("firebaseMessagingToken")
                            } ?: ""
                        )
                    )
                    authenticationViewModel.getUserFromDB(
                        user.id,
                        sharedPref.getString(MESSAGE_TOKEN)
                    )
                }.start()
            }.start()
        }
    }

    private fun animateLoading() {
        val welcomeText = binding.welcomeFirstText
        val welcomeSubText = binding.welcomeFirstSubText
        val userNameText = binding.userNameTextEdit
        val animDuration = 300L

        welcomeText.scaleX = 0f
        welcomeText.scaleY = 0f
        welcomeSubText.scaleX = 0f
        welcomeSubText.scaleY = 0f
        userNameText.alpha = 0f

        welcomeText.animate().scaleX(1f).scaleY(1f).setDuration(animDuration).withEndAction {
            welcomeSubText.animate().scaleX(1f).scaleY(1f).setDuration(animDuration).withEndAction {
                userNameText.animate().alpha(1f).setDuration(animDuration).start()
            }.start()
        }.start()
    }

    companion object {
        const val ARG_USER = "user"
    }
}