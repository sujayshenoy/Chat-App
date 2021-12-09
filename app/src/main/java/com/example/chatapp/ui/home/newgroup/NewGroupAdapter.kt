package com.example.chatapp.ui.home.newgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.User

class NewGroupAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<MemberViewHolder>() {
    private lateinit var onItemCheckedListener: OnItemChecked

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.member_view_holder, parent, false)
        return MemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.name.text = currentUser.name

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onItemCheckedListener.onItemChecked(position)
            } else {
                onItemCheckedListener.onItemUnchecked(position)
            }
        }

        holder.memberView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setOnItemCheckedListener(listener: OnItemChecked) {
        onItemCheckedListener = listener
    }

    interface OnItemChecked {
        fun onItemChecked(position: Int)
        fun onItemUnchecked(position: Int)
    }
}