package com.example.chatapp.ui.home.chat

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.ui.home.HomeViewModel
import com.example.chatapp.ui.home.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog
    private lateinit var adapter: RecyclerAdapter
    var chatListener: ChatFragmentHostListener? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatBinding.bind(view)
        context?.let {
            logger = LoggerImpl("Chat Fragment")
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
            dialog.show()
        } ?: Log.e("ChatFragment", "Empty Context")

        initViewModel()
        initRecyclerView()
        initObservers()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {
        activity?.let { activity ->
            val sharedModel = ViewModelProvider(activity)[HomeViewModel::class.java]
            sharedModel.currentUser?.let {
                chatViewModel = ViewModelProvider(activity, ViewModelFactory(ChatViewModel(it.id)))[ChatViewModel::class.java]
            }
        } ?: logger.logError("Empty Activity")
    }

    @ExperimentalCoroutinesApi
    private fun initObservers() {
        chatViewModel.userListChanged.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = RecyclerAdapter(chatViewModel.userList)
        val recyclerView = binding.chatsRecyclerView
        context?.let { context ->
            val layoutManager = LinearLayoutManager(context)
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = true
            recyclerView.layoutManager = layoutManager
        } ?: logger.logError("Empty Context")
        adapter.setOnItemClickListener(object : RecyclerItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedUser = chatViewModel.userList[position]
                chatListener?.onChatItemClicked(selectedUser)
            }
        })
        recyclerView.adapter = adapter
    }

    interface ChatFragmentHostListener {
        fun onChatItemClicked(selectedUser: User)
    }
}