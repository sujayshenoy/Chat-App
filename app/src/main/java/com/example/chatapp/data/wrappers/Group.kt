package com.example.chatapp.data.wrappers

import java.io.Serializable

data class Group(
    var id: String,
    var name: String,
    var channelId: String,
    var members: ArrayList<String>
) : Serializable
