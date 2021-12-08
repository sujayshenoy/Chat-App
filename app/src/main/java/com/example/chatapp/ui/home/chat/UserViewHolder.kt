package com.example.chatapp.ui.home.chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView

class UserViewHolder(itemView: View, itemClickListener: RecyclerItemClickListener): RecyclerView.ViewHolder(itemView) {
    val userName: TextView = itemView.findViewById(R.id.userName)
    val userAvatar: CircleImageView = itemView.findViewById(R.id.userAvatar)

    init {
        itemView.setOnClickListener {
            itemClickListener.onItemClick(adapterPosition)
        }
    }
}