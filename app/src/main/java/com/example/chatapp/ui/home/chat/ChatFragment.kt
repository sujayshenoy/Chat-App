package com.example.chatapp.ui.home.chat

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.Logger
import com.example.chatapp.databinding.FragmentChatBinding

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var logger: Logger
    private lateinit var dialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatBinding.bind(view)
        context?.let {
            logger = Logger.getInstance(it)
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
            dialog.show()
        } ?: Log.e("ChatFragment","Empty Context")
        activity?.let {
            chatViewModel = ViewModelProvider(it)[ChatViewModel::class.java]
            chatViewModel.getUserList(it)
        } ?: logger.logError("Empty Activity")

        initObservers()
    }

    private fun initObservers() {
        chatViewModel.getUserListStatus.observe(viewLifecycleOwner) {
            initRecyclerView()
            dialog.dismiss()
        }
    }

    private fun initRecyclerView() {
        chatViewModel.userList?.let {
            val adapter = RecyclerAdapter(it)
            val recyclerView = binding.chatsRecyclerView
            context?.let { context ->
                recyclerView.layoutManager = LinearLayoutManager(context)
            } ?: logger.logError("Empty Context")
            recyclerView.adapter = adapter
        } ?: logger.logError("User fetch Failed")
    }
}