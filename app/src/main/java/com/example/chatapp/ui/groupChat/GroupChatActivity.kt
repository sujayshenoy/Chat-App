package com.example.chatapp.ui.groupChat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.ActivityChatScreenBinding
import com.example.chatapp.ui.home.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var adapter: GroupChatRecyclerAdapter
    private lateinit var sender: User
    private lateinit var group: Group
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

        binding.toolbar.title = group.name
        initRecyclerView()
        initClickListeners()
        initObservers()
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = GroupChatRecyclerAdapter(groupChatViewModel.messageList, sender.id)
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
            adapter.notifyDataSetChanged()
            if(adapter.itemCount != 0) {
                binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
            logger.logInfo("Messages: ${groupChatViewModel.messageList}")
        }
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
    }

    private fun getDataFromIntent() {
        intent.extras?.let {
            sender = it.getSerializable(ARG_USER_SENDER) as User
            group = it.getSerializable(ARG_GROUP) as Group
        } ?: logger.logError("Receiver User Null or Group Null")
    }
}