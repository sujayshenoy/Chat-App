package com.example.chatapp.ui.home.groupChat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.ui.home.common.viewholders.ChatViewHolder

class GroupChatRecyclerAdapter(
    private val messageList: ArrayList<Message>,
    private val memberList: ArrayList<User>,
    private val senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder = when (viewType) {
            0 -> {
                val itemView = layoutInflater.inflate(R.layout.sender_view_holder, parent, false)
                ChatViewHolder(itemView)
            }
            else -> {
                val itemView =
                    layoutInflater.inflate(R.layout.group_receiver_view_holder, parent, false)
                GroupChatReceiverViewHolder(itemView)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when (getItemViewType(position)) {
            0 -> {
                holder as ChatViewHolder
                holder.message.text = currentMessage.content
            }
            else -> {
                holder as GroupChatReceiverViewHolder
                memberList.forEach {
                    if (it.id == currentMessage.senderId) {
                        holder.senderName.text = it.name
                    }
                }
                holder.message.text = currentMessage.content
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == senderId) 0 else 1
    }
}