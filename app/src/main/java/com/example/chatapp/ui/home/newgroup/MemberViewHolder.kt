package com.example.chatapp.ui.home.newgroup

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView

class MemberViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val avatar: CircleImageView = itemView.findViewById(R.id.memberAvatar)
    val name: TextView = itemView.findViewById(R.id.memberName)
    val checkBox: CheckBox = itemView.findViewById(R.id.memberCheckBox)
    val memberView: ConstraintLayout = itemView.findViewById(R.id.memberView)
}