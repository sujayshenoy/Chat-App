package com.example.chatapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.Logger
import com.example.chatapp.common.Utilities
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityAuthenticationBinding
import com.example.chatapp.ui.authentication.NewUserFragment.Companion.ARG_USER
import com.example.chatapp.ui.home.HomeActivity

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logger = Logger.getInstance(this@AuthenticationActivity)
        authenticationViewModel =
            ViewModelProvider(this@AuthenticationActivity)[AuthenticationViewModel::class.java]

        initObservers()
        initNavigators()
        authenticationViewModel.goToLoginFragment()
    }

    private fun initNavigators() {
        authenticationViewModel.goToLoginFragment.observe(this@AuthenticationActivity) {
            goToLoginFragment()
        }

        authenticationViewModel.goToOtpFragment.observe(this@AuthenticationActivity) {
            goToOtpFragment()
        }

        authenticationViewModel.goToNewUserFragment.observe(this@AuthenticationActivity) {
            goToNewUserFragment(it)
        }
    }

    private fun initObservers() {
        authenticationViewModel.goToHomeScreen.observe(this@AuthenticationActivity) {
            val intent = Intent(this@AuthenticationActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        authenticationViewModel.sendOtpStatus.observe(this@AuthenticationActivity) {
            authenticationViewModel.goToOtpFragment()
        }

        authenticationViewModel.verifyUserStatus.observe(this@AuthenticationActivity) { user ->
            if (user != null) {
                Utilities.displayLongToast(this@AuthenticationActivity, "Authentication Successful")
                if (user.isNewUser) {
                    authenticationViewModel.goToNewUserFragment()
                } else {
                    authenticationViewModel.getUserFromDB(this@AuthenticationActivity, user.id)
                }
            } else {
                Utilities.displayLongToast(this@AuthenticationActivity, "Authentication Failed")
            }
            logger.logInfo("Logged in User: $user")
        }

        authenticationViewModel.getUserFromDbStatus.observe(this@AuthenticationActivity) {
            it?.let {
                authenticationViewModel.goToHomeScreen()
            } ?: authenticationViewModel.goToNewUserFragment()
        }
    }

    private fun goToLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authenticationFragmentContainer, LoginFragment()).commit()
    }

    private fun goToOtpFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.authenticationFragmentContainer, OtpFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun goToNewUserFragment(user: User) {
        val bundle = Bundle()
        val fragment = NewUserFragment()
        bundle.putSerializable(ARG_USER, user)
        fragment.arguments = bundle

        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_right,
            )
            .replace(R.id.authenticationFragmentContainer, fragment)
            .commit()
    }
}