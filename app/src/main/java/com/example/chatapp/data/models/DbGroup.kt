package com.example.chatapp.data.models

data class DbGroup(
    var name: String,
    var channelId: String,
    var members: ArrayList<String>
) {
    companion object {
        const val GROUP_NAME = "name"
        const val GROUP_CHANNEL = "channelId"
        const val GROUP_MEM = "members"
    }
}