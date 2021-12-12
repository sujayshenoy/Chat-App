package com.example.chatapp.data.repo.repoInterfaces

import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendTextMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        message: String
    ): Message?

    fun getMessages(senderId: String, receiverId: String): Flow<Message?>
    suspend fun sendGroupTextMessage(senderId: String, channelId: String, message: String): Message?
    fun getGroupMessages(channelId: String): Flow<Message?>
    suspend fun sendImageMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): Message?

    suspend fun sendGroupImageMessage(
        senderId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): Message?

    suspend fun sendPushNotificationToUser(
        userToken: String,
        title: String,
        message: String,
        imageUrl: String
    )

    suspend fun sendPushNotificationToGroup(
        members: ArrayList<String>,
        title: String,
        message: String,
        imageUrl: String
    )
}