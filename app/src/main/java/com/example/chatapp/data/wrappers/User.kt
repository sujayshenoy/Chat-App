package com.example.chatapp.data.wrappers

import java.io.Serializable

data class User(
    var id: String,
    var name: String = "",
    var phone: String = "",
    var isNewUser: Boolean = false,
    var messageToken: String = "",
    var avatar: String = ""
) : Serializable
