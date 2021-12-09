package com.example.chatapp.ui.home

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.ui.home.RecyclerItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class EntityViewHolder(itemView: View, itemClickListener: RecyclerItemClickListener): RecyclerView.ViewHolder(itemView) {
    val entityName: TextView = itemView.findViewById(R.id.entityName)
    val entityAvatar: CircleImageView = itemView.findViewById(R.id.entityAvatar)

    init {
        itemView.setOnClickListener {
            itemClickListener.onItemClick(adapterPosition)
        }
    }
}