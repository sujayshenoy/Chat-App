package com.example.chatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.ui.home.chats.ChatsFragment
import com.example.chatapp.ui.home.common.listeners.ChatFragmentHostListener
import com.example.chatapp.ui.home.groups.GroupsFragment

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentHostListener: ChatFragmentHostListener<Any>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            ChatsFragment().also {
                it.chatListener = fragmentHostListener as ChatFragmentHostListener<User>
            }
        } else {
            GroupsFragment().also {
                it.groupChatListener = fragmentHostListener as ChatFragmentHostListener<Group>
            }
        }
    }
}