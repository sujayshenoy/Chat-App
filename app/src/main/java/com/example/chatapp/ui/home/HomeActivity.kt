package com.example.chatapp.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.common.Logger
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.ui.authentication.AuthenticationActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logger = Logger.getInstance(this@HomeActivity)
        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.show()
        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]
        homeViewModel.fetchCurrentUserData(this@HomeActivity)

        initClickListeners()
        initObservers()
        initNavigators()
    }

    private fun initNavigators() {
        homeViewModel.goToLoginScreen.observe(this@HomeActivity) {
            val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initObservers() {
        homeViewModel.logoutStatus.observe(this@HomeActivity) {
            homeViewModel.goToLoginScreen()
        }

        homeViewModel.getUserFromDbStatus.observe(this@HomeActivity) {
            homeViewModel.currentUser?.let {
                binding.loginInfo.text = "Logged in: ${it.name}"
                dialog.dismiss()
            }
        }
    }

    private fun initClickListeners() {
        binding.logoutButton.setOnClickListener {
            homeViewModel.logout(this@HomeActivity)
        }
    }
}