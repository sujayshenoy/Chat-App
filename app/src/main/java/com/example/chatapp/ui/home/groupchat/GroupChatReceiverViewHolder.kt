package com.example.chatapp.ui.home.groupchat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R

class GroupChatReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val senderName: TextView = itemView.findViewById(R.id.senderName)
    val message: TextView = itemView.findViewById(R.id.message)
    val messageImage: ImageView = itemView.findViewById(R.id.messageImage)
}