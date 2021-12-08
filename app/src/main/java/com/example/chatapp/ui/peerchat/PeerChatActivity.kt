package com.example.chatapp.ui.peerchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityPeerChatBinding
import com.example.chatapp.ui.home.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PeerChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPeerChatBinding
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

        binding = ActivityPeerChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        peerChatViewModel = ViewModelProvider(
            this@PeerChatActivity,
            ViewModelFactory(PeerChatViewModel(sender.id, receiver.id))
        )[PeerChatViewModel::class.java]
        logger = LoggerImpl("PeerChat Activity")

        binding.toolbar.title = receiver.name
        initRecyclerView()
        initClickListeners()
        initObservers()
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = ChatRecyclerAdapter(peerChatViewModel.messageList, sender.id)
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
            if(adapter.itemCount != 0) {
                binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
            logger.logInfo("Messages: ${peerChatViewModel.messageList}")
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
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            receiver = it.getSerializable(ARG_USER_RECEIVER) as User
            sender = it.getSerializable(ARG_USER_SENDER) as User
        } ?: logger.logError("Receiver User Null")
    }
}