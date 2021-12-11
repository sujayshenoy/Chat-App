package com.example.chatapp.ui.home.peerchat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.ui.home.common.viewholders.ChatViewHolder

class ChatRecyclerAdapter(
    private val context: Context,
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
        if (currentMessage.contentType == CONTENT_TYPE_TEXT) {
            holder.message.text = currentMessage.content
            holder.message.visibility = View.VISIBLE
            holder.messageImage.visibility = View.GONE
        } else {
            holder.message.visibility = View.GONE
            holder.messageImage.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(currentMessage.content)
                .fitCenter()
                .thumbnail(Glide.with(context).load(R.drawable.spinning_loading))
                .into(holder.messageImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == senderId) 0 else 1
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}