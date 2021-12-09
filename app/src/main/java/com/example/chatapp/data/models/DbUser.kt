package com.example.chatapp.data.models

data class DbUser(
    var name: String = "",
    var phone: String = "",
    var firebaseMessagingToken: String = ""
) {
    companion object {
        const val USER_NAME = "name"
        const val USER_PHONE = "phone"
        const val FIREBASE_MESSAGE_TOKEN = "firebaseMessagingToken"
    }
}
