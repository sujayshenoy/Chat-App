package com.example.chatapp.data.repo.repoInterfaces

import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        message: String
    ): String

    fun getMessages(senderId: String, receiverId: String): Flow<Message?>
    suspend fun sendGroupMessage(senderId: String, channelId: String, message: String): String
    fun getGroupMessages(channelId: String): Flow<Message?>
}