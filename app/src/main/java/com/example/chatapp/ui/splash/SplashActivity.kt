package com.example.chatapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.USER_ID
import com.example.chatapp.common.sharedpreferences.SharedPrefUtilImpl
import com.example.chatapp.databinding.ActivitySplashBinding
import com.example.chatapp.ui.authentication.AuthenticationActivity
import com.example.chatapp.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val splashIcon = binding.splashIcon
        splashIcon.alpha = 0f
        splashIcon.animate().alpha(1f).setDuration(1500).setInterpolator(DecelerateInterpolator())
            .withEndAction {
                if (SharedPrefUtilImpl.getInstance(this@SplashActivity)
                        .getString(USER_ID).isEmpty()
                ) {
                    val intent = Intent(this@SplashActivity, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .start()
    }
}