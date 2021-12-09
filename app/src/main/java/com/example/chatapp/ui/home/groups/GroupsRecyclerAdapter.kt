package com.example.chatapp.ui.home.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.ui.home.common.viewholders.EntityViewHolder
import com.example.chatapp.ui.home.common.listeners.RecyclerItemClickListener

class GroupsRecyclerAdapter(private val groupList: ArrayList<Group>) :
    RecyclerView.Adapter<EntityViewHolder>() {
    private lateinit var itemClickListener: RecyclerItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.entity_view_holder, parent, false)

        return EntityViewHolder(itemView, itemClickListener)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.entityName.text = currentGroup.name
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    fun setOnItemClickListener(listener: RecyclerItemClickListener) {
        itemClickListener = listener
    }
}