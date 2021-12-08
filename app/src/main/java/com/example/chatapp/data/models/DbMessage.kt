package com.example.chatapp.data.models

data class DbMessage(
    var senderId: String,
    var receiverId: String,
    var content: String,
    var contentType: String,
    var timeStamp: Long
) {
    companion object {
        const val SENDER_ID = "senderId"
        const val RECEIVER_ID = "receiverId"
        const val CONTENT = "content"
        const val CONTENT_TYPE = "contentType"
        const val TIMESTAMP = "timeStamp"
    }
}
