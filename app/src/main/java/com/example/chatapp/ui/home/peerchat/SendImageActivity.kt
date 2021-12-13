package com.example.chatapp.ui.home.peerchat

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.databinding.SendImageMessageBinding

class SendImageActivity : AppCompatActivity() {
    private lateinit var binding: SendImageMessageBinding
    private lateinit var selectedImageByteArray: ByteArray
    private val logger: Logger = LoggerImpl(this::class.java.simpleName)

    companion object {
        const val SELECTED_IMAGE = "selectedImage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SendImageMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        getDataFromIntent()
        showImage()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.sendImageButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra(SELECTED_IMAGE, selectedImageByteArray)
            setResult(0, intent)
            finish()
        }
    }

    private fun showImage() {
        Glide.with(this@SendImageActivity)
            .load(selectedImageByteArray)
            .into(binding.pickedImage)
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            selectedImageByteArray = it.getByteArray(SELECTED_IMAGE) ?: ByteArray(0)
        }
    }
}