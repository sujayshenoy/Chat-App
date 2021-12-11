package com.example.chatapp.ui.home.groupChat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.ui.home.common.viewholders.ChatViewHolder

class GroupChatRecyclerAdapter(
    private val context: Context,
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
            else -> {
                holder as GroupChatReceiverViewHolder
                memberList.forEach {
                    if (it.id == currentMessage.senderId) {
                        holder.senderName.text = it.name
                    }
                }
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