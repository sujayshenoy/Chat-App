package com.example.chatapp.ui.home.groupChat

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.common.IMAGE_CONFIRM_REQUEST_CODE
import com.example.chatapp.common.PICK_IMAGE_FROM_GALLERY_REQUEST_CODE
import com.example.chatapp.common.STORAGE_PERMISSION_REQUEST_CODE
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityChatScreenBinding
import com.example.chatapp.ui.home.common.viewmodel.ViewModelFactory
import com.example.chatapp.ui.home.peerchat.SendImageActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var adapter: GroupChatRecyclerAdapter
    private lateinit var sender: User
    private lateinit var group: Group
    private lateinit var dialog: Dialog
    private val logger: Logger = LoggerImpl("GroupChat Activity")

    companion object {
        const val ARG_USER_SENDER = "userSender"
        const val ARG_GROUP = "group"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        groupChatViewModel = ViewModelProvider(
            this@GroupChatActivity,
            ViewModelFactory(GroupChatViewModel(group))
        )[GroupChatViewModel::class.java]
        dialog = Dialog(this@GroupChatActivity)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.show()

        binding.toolbar.title = group.name
        initClickListeners()
        initObservers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST_CODE && data != null) {
            data.data?.let {
                handleImageData(it)
            }
        }

        if (requestCode == IMAGE_CONFIRM_REQUEST_CODE && data != null) {
            data.extras?.let {
                it.getByteArray(SendImageActivity.SELECTED_IMAGE)?.let {
                    groupChatViewModel.sendImageMessage(sender.id, it)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                logger.logInfo("Storage Permission Granted")
            } else {
                logger.logInfo("Permission Denied")
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = GroupChatRecyclerAdapter(
            this@GroupChatActivity,
            groupChatViewModel.messageList,
            groupChatViewModel.memberList,
            sender.id
        )
        val recyclerView = binding.chatRecyclerView
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this@GroupChatActivity)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        recyclerView.post { recyclerView.smoothScrollToPosition(adapter.itemCount) }
    }

    @ExperimentalCoroutinesApi
    private fun initObservers() {
        groupChatViewModel.newMessageStatus.observe(this@GroupChatActivity) {
            if (this::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
                if (adapter.itemCount != 0) {
                    binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        groupChatViewModel.fetchMembeListStatus.observe(this@GroupChatActivity) {
            initRecyclerView()
            dialog.dismiss()
        }

        groupChatViewModel.sendMessageStatus.observe(this@GroupChatActivity) {
            groupChatViewModel.sendPushNotification(sender.name, it)
        }
    }

    private fun handleImageData(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos)
        val byteArray = baos.toByteArray()
        val intent = Intent(this@GroupChatActivity, SendImageActivity::class.java)
        intent.putExtra(SendImageActivity.SELECTED_IMAGE, byteArray)
        startActivityForResult(intent, IMAGE_CONFIRM_REQUEST_CODE)
    }

    @ExperimentalCoroutinesApi
    private fun initClickListeners() {
        binding.sendMessageButton.setOnClickListener {
            val message = binding.sendMessageEditText.text.toString()
            if (message.isNotEmpty()) {
                binding.sendMessageEditText.setText("")
                groupChatViewModel.sendMessage(sender.id, message)
            } else {
                binding.sendMessageEditText.error = getString(R.string.Empty_message_error)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.sendImageButton.setOnClickListener {
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

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY_REQUEST_CODE)
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            sender = it.getSerializable(ARG_USER_SENDER) as User
            group = it.getSerializable(ARG_GROUP) as Group
        } ?: logger.logError("Receiver User Null or Group Null")
    }
}