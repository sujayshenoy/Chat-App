package com.example.chatapp.data.repo.repoInterfaces

import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendTextMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        message: String
    ): String
    fun getMessages(senderId: String, receiverId: String): Flow<Message?>
    suspend fun sendGroupTextMessage(senderId: String, channelId: String, message: String): String
    fun getGroupMessages(channelId: String): Flow<Message?>
    suspend fun sendImageMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): String
    suspend fun sendGroupImageMessage(
        senderId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): String
}