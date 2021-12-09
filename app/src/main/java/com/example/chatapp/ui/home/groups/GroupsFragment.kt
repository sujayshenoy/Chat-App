package com.example.chatapp.ui.home.groups

import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.databinding.FragmentGroupChatBinding
import com.example.chatapp.ui.home.ChatFragmentHostListener
import com.example.chatapp.ui.home.HomeViewModel
import com.example.chatapp.ui.home.RecyclerItemClickListener
import com.example.chatapp.ui.home.newgroup.NewGroupActivity
import com.example.chatapp.ui.home.newgroup.NewGroupActivity.Companion.CURRENT_USER
import com.example.chatapp.ui.home.newgroup.NewGroupActivity.Companion.USER_LIST
import kotlinx.coroutines.ExperimentalCoroutinesApi

class GroupsFragment : Fragment(R.layout.fragment_group_chat) {
    private lateinit var binding: FragmentGroupChatBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: GroupsRecyclerAdapter
    private lateinit var dialog: Dialog
    private val logger: Logger = LoggerImpl("GroupChatFragment")
    var groupChatListener: ChatFragmentHostListener<Group>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentGroupChatBinding.bind(view)
        context?.let {
            dialog = Dialog(it)
            dialog.setContentView(R.layout.progress_dialog)
            dialog.show()
        } ?: logger.logError("Empty Context")

        initViewModel()
        initRecyclerView()
        initObservers()
        initClickListeners()
    }

    @ExperimentalCoroutinesApi
    private fun initClickListeners() {
        binding.newGroupButton.setOnClickListener {
            context?.let { context ->
                val intent = Intent(context, NewGroupActivity::class.java)
                intent.putExtra(USER_LIST, homeViewModel.userList)
                intent.putExtra(CURRENT_USER, homeViewModel.currentUser)
                startActivity(intent)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {
        activity?.let { activity ->
            homeViewModel = ViewModelProvider(activity)[HomeViewModel::class.java]
        } ?: logger.logError("Empty Activity")
    }

    @ExperimentalCoroutinesApi
    private fun initObservers() {
        homeViewModel.groupListChanged.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

    @ExperimentalCoroutinesApi
    private fun initRecyclerView() {
        adapter = GroupsRecyclerAdapter(homeViewModel.groupList)
        val recyclerView = binding.groupChatsRecyclerView
        context?.let { context ->
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
        } ?: logger.logError("Empty Context")
        adapter.setOnItemClickListener(object : RecyclerItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedGroup = homeViewModel.groupList[position]
                groupChatListener?.onChatItemClicked(selectedGroup)
            }
        })
        recyclerView.adapter = adapter
    }
}