package com.example.chatapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.common.Utilities
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.USER_ID
import com.example.chatapp.common.sharedpreferences.SharedPrefUtilImpl
import com.example.chatapp.databinding.ActivityAuthenticationBinding
import com.example.chatapp.ui.authentication.NewUserFragment.Companion.ARG_USER
import com.example.chatapp.ui.home.HomeActivity

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var logger: Logger
    private lateinit var sharedPref: SharedPrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logger = LoggerImpl("Authentication Activity")
        authenticationViewModel =
            ViewModelProvider(this@AuthenticationActivity)[AuthenticationViewModel::class.java]
        sharedPref = SharedPrefUtilImpl.getInstance(this@AuthenticationActivity)

        initObservers()
        goToLoginFragment()
    }

    private fun initObservers() {
        authenticationViewModel.sendOtpStatus.observe(this@AuthenticationActivity) {
            goToOtpFragment()
        }

        authenticationViewModel.verifyUserStatus.observe(this@AuthenticationActivity) { user ->
            if (user != null) {
                Utilities.displayLongToast(this@AuthenticationActivity, "Authentication Successful")
                if (user.isNewUser) {
                    goToNewUserFragment()
                } else {
                    authenticationViewModel.getUserFromDB(user.id)
                }
            } else {
                Utilities.displayLongToast(this@AuthenticationActivity, "Authentication Failed")
            }
            logger.logInfo("Logged in User: $user")
        }

        authenticationViewModel.getUserFromDbStatus.observe(this@AuthenticationActivity) {
            it?.let {
                sharedPref.addString(USER_ID, it.id)
                goToHomeScreen()
            } ?: goToNewUserFragment()
        }
    }

    private fun goToHomeScreen() {
        val intent = Intent(this@AuthenticationActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
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

    private fun goToNewUserFragment() {
        val bundle = Bundle()
        val fragment = NewUserFragment()
        bundle.putSerializable(ARG_USER, authenticationViewModel.loggedInUser)
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