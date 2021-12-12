package com.example.chatapp.data.network.retrofit

data class PushMessage(
    val to: String,
    val notification: PushContent
)
