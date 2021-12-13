package com.example.chatapp.ui.home.peerchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.common.IMAGE_CONFIRM_REQUEST_CODE
import com.example.chatapp.common.PICK_IMAGE_FROM_GALLERY_REQUEST_CODE
import com.example.chatapp.common.STORAGE_PERMISSION_REQUEST_CODE
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityChatScreenBinding
import com.example.chatapp.ui.home.common.viewmodel.ViewModelFactory
import com.example.chatapp.ui.home.peerchat.ReceiverInfoActivity.Companion.RECEIVER_USER
import com.example.chatapp.ui.home.peerchat.SendImageActivity.Companion.SELECTED_IMAGE
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream

class PeerChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var peerChatViewModel: PeerChatViewModel
    private lateinit var adapter: ChatRecyclerAdapter
    private lateinit var receiver: User
    private lateinit var sender: User
    private lateinit var logger: LoggerImpl

    companion object {
        const val ARG_USER_RECEIVER = "userReceiver"
        const val ARG_USER_SENDER = "userSender"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        peerChatViewModel = ViewModelProvider(
            this@PeerChatActivity,
            ViewModelFactory(PeerChatViewModel(sender.id, receiver.id))
        )[PeerChatViewModel::class.java]
        logger = LoggerImpl("PeerChat Activity")

        binding.chatReceiverName.text = receiver.name
        binding.chatReceiverAvatar.visibility = View.VISIBLE
        Glide.with(this@PeerChatActivity)
            .load(receiver.avatar)
            .placeholder(R.drawable.ic_user_avatar_placeholder)
            .into(binding.chatReceiverAvatar)
        initRecyclerView()
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
                it.getByteArray(SELECTED_IMAGE)?.let {
                    peerChatViewModel.sendImageMessage(sender.id, receiver.id, it)
                }
            }
        }
    }

    private fun handleImageData(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 75, baos)
        val byteArray = baos.toByteArray()
        val intent = Intent(this@PeerChatActivity, SendImageActivity::class.java)
        intent.putExtra(SELECTED_IMAGE, byteArray)
        startActivityForResult(intent, IMAGE_CONFIRM_REQUEST_CODE)
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

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter =
            ChatRecyclerAdapter(this@PeerChatActivity, peerChatViewModel.messageList, sender.id)
        val recyclerView = binding.chatRecyclerView
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this@PeerChatActivity)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        recyclerView.post { recyclerView.smoothScrollToPosition(adapter.itemCount) }
    }

    @ExperimentalCoroutinesApi
    private fun initObservers() {
        peerChatViewModel.newMessageStatus.observe(this@PeerChatActivity) {
            adapter.notifyDataSetChanged()
            if (adapter.itemCount != 0) {
                binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
            logger.logInfo("Messages: ${peerChatViewModel.messageList}")
        }

        peerChatViewModel.sendMessageStatus.observe(this@PeerChatActivity) {
            peerChatViewModel.sendPushNotification(receiver.messageToken, receiver.name, it)
        }
    }

    @ExperimentalCoroutinesApi
    private fun initClickListeners() {
        binding.sendMessageButton.setOnClickListener {
            val message = binding.sendMessageEditText.text.toString()
            if (message.isNotEmpty()) {
                peerChatViewModel.sendMessage(
                    sender.id,
                    receiver.id,
                    message
                )
                binding.sendMessageEditText.setText("")
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

        binding.chatReceiverAvatar.setOnClickListener {
            showReceiverInfo()
        }

        binding.chatReceiverName.setOnClickListener {
            showReceiverInfo()
        }
    }

    private fun showReceiverInfo() {
        val intent = Intent(this@PeerChatActivity, ReceiverInfoActivity::class.java)
        intent.putExtra(RECEIVER_USER, receiver)
        startActivity(intent)
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY_REQUEST_CODE)
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            receiver = it.getSerializable(ARG_USER_RECEIVER) as User
            sender = it.getSerializable(ARG_USER_SENDER) as User
        } ?: logger.logError("Receiver User Null")
    }
}