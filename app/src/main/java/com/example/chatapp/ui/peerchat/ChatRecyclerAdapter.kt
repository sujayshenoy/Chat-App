package com.example.chatapp.ui.peerchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.Message

class ChatRecyclerAdapter(
    private val messageList: ArrayList<Message>,
    private val senderId: String
) : RecyclerView.Adapter<ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = when (viewType) {
            0 -> {
                layoutInflater.inflate(R.layout.sender_view_holder, parent, false)
            }
            else -> {
                layoutInflater.inflate(R.layout.receiver_view_holder, parent, false)
            }
        }
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentMessage = messageList[position]
        holder.message.text = currentMessage.content
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == senderId) 0 else 1
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}