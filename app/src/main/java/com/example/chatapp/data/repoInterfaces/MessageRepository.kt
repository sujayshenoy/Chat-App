package com.example.chatapp.data.repoInterfaces

interface MessageRepository {
    suspend fun sendMessage(senderId: String, receiverId:String, message: String): String
}