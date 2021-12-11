package com.example.chatapp.ui.home.common.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageImage: ImageView = itemView.findViewById(R.id.messageImage)
    val message: TextView = itemView.findViewById(R.id.message)
}