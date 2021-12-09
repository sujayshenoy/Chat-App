package com.example.chatapp.ui.home.chats

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.ui.home.ChatFragmentHostListener
import com.example.chatapp.ui.home.HomeViewModel
import com.example.chatapp.ui.home.RecyclerItemClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatsFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private lateinit var homeViewModel: HomeViewModel
    private val logger: Logger = LoggerImpl("Chat Fragment")
    private lateinit var dialog: Dialog
    private lateinit var adapter: ChatsRecyclerAdapter
    var chatListener: ChatFragmentHostListener<User>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatBinding.bind(view)
        context?.let {
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
            dialog.show()
        } ?: logger.logError("Empty Context")

        initViewModel()
        initRecyclerView()
        initObservers()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {
        activity?.let { activity ->
            homeViewModel = ViewModelProvider(activity)[HomeViewModel::class.java]
        } ?: logger.logError("Empty Activity")
    }

    @ExperimentalCoroutinesApi
    private fun initObservers() {
        homeViewModel.userListChanged.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = ChatsRecyclerAdapter(homeViewModel.userList)
        val recyclerView = binding.chatsRecyclerView
        context?.let { context ->
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
        } ?: logger.logError("Empty Context")
        adapter.setOnItemClickListener(object : RecyclerItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedUser = homeViewModel.userList[position]
                chatListener?.onChatItemClicked(selectedUser)
            }
        })
        recyclerView.adapter = adapter
    }
}