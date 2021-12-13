package com.example.chatapp.ui.home.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.common.PICK_IMAGE_FROM_GALLERY_REQUEST_CODE
import com.example.chatapp.common.STORAGE_PERMISSION_REQUEST_CODE
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityProfileBinding
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val logger: Logger = LoggerImpl(this::class.java.simpleName)
    private lateinit var currentUser: User
    private lateinit var profileViewModel: ProfileViewModel

    companion object {
        const val CURRENT_USER = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        profileViewModel = ViewModelProvider(this@ProfileActivity)[ProfileViewModel::class.java]

        getDataFromIntent()
        updateUI()
        initClickListeners()
        initObservers()
        Glide.with(this@ProfileActivity)
            .load(currentUser.avatar)
            .placeholder(R.drawable.ic_user_avatar_placeholder)
            .into(binding.userAvatar)
    }

    private fun initObservers() {
        profileViewModel.setUserAvatarStatus.observe(this@ProfileActivity) {
            currentUser.avatar = it
            Glide.with(this@ProfileActivity)
                .load(currentUser.avatar)
                .placeholder(R.drawable.ic_user_avatar_placeholder)
                .into(binding.userAvatar)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST_CODE && data != null) {
            data.data?.let {
                handleImageData(it)
            }
        }
    }

    private fun handleImageData(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos)
        val byteArray = baos.toByteArray()
        profileViewModel.setUserAvatar(currentUser.id, byteArray)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                logger.logInfo("Permission Denied")
            } else {
                logger.logInfo("Storage Permission Granted")
                pickImage()
            }
        }
    }

    private fun initClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent()
            intent.putExtra(CURRENT_USER, currentUser)
            setResult(0, intent)
            finish()
        }

        binding.userNameFlow.setOnClickListener {
            showEditNameDialog()
        }

        binding.updateAvatarButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                logger.logInfo("Requesting Permission")
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showEditNameDialog() {
        val userNameView = LayoutInflater.from(this@ProfileActivity)
            .inflate(R.layout.dialog_edit_username, null)
        val userNameEditText: EditText = userNameView.findViewById(R.id.userNameTextEdit)
        userNameEditText.setText(currentUser.name)
        val alertDialog =
            AlertDialog.Builder(this@ProfileActivity)
                .setView(userNameView)
                .setPositiveButton(
                    "Save"
                ) { _, _ ->
                    profileViewModel.updateUserName(
                        currentUser.id,
                        userNameEditText.text.toString()
                    )
                    currentUser.name = userNameEditText.text.toString()
                    binding.userNameText.text = currentUser.name
                }.setNegativeButton(
                    "Cancel"
                ) { _, _ -> }.create()
        alertDialog.window?.let {
            it.setBackgroundDrawable(
                resources.getDrawable(
                    R.drawable.app_primary_background,
                    theme
                )
            )
            val attr = it.attributes
            attr.gravity = Gravity.BOTTOM
            it.attributes = attr
        }
        alertDialog.show()
        val positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(resources.getColor(R.color.primary_text, theme))
        negativeButton.setTextColor(resources.getColor(R.color.primary_text, theme))
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY_REQUEST_CODE)
    }

    private fun updateUI() {
        if (this::currentUser.isInitialized) {
            binding.userNameText.text = currentUser.name
            binding.userPhoneText.text = currentUser.phone
        }
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            currentUser = it.getSerializable(CURRENT_USER) as User
        }
    }
}