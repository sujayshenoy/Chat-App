package com.example.chatapp.ui.home.common.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val message: TextView = itemView.findViewById(R.id.message)
}