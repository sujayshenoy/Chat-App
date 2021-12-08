package com.example.chatapp.ui.home.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.User

class RecyclerAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserViewHolder>() {
    private lateinit var itemClickListener: RecyclerItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.users_view_holder, parent, false)

        return UserViewHolder(itemView, itemClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userName.text = currentUser.name
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setOnItemClickListener(listener: RecyclerItemClickListener) {
        itemClickListener = listener
    }

}