package com.example.chatapp.ui.home.chats

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.ui.home.common.viewholders.EntityViewHolder
import com.example.chatapp.ui.home.common.listeners.RecyclerItemClickListener

class ChatsRecyclerAdapter(private val context: Context,private val userList: ArrayList<User>) :
    RecyclerView.Adapter<EntityViewHolder>() {
    private lateinit var itemClickListener: RecyclerItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.entity_view_holder, parent, false)

        return EntityViewHolder(itemView, itemClickListener)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        val currentUser = userList[position]
        Log.i("ChatsRecyclerDapter",currentUser.toString())
        Glide.with(context)
            .load(currentUser.avatar)
            .placeholder(R.drawable.ic_user_avatar_placeholder)
            .into(holder.entityAvatar)
        holder.entityName.text = currentUser.name
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setOnItemClickListener(listener: RecyclerItemClickListener) {
        itemClickListener = listener
    }
}