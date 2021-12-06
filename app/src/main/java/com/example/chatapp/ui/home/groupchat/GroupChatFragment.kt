package com.example.chatapp.ui.home.groupchat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.R

class GroupChatFragment : Fragment() {

    companion object {
        fun newInstance() = GroupChatFragment()
    }

    private lateinit var viewModel: GroupChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupChatViewModel::class.java)
        // TODO: Use the ViewModel
    }

}