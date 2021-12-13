package com.example.chatapp.ui.home.peerchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityReceiverInfoBinding

class ReceiverInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiverInfoBinding
    private lateinit var receiver: User

    companion object {
        const val RECEIVER_USER = "currentUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceiverInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataFromIntent()
        initClickListeners()
        binding.receiverName.text = receiver.name
        Glide.with(this@ReceiverInfoActivity)
            .load(receiver.avatar)
            .placeholder(R.drawable.ic_user_avatar_placeholder)
            .into(binding.receiverAvatar)
    }

    private fun initClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            receiver = it.getSerializable(RECEIVER_USER) as User
        }
    }
}