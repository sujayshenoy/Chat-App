package com.example.chatapp.data.wrappers

data class Message(
    var senderId: String,
    var content: String,
    var contentType: String,
    var timeStamp: Long
)
