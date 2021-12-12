package com.example.chatapp.common.sharedpreferences

interface SharedPrefUtil {
    companion object {
        const val USER_ID = "userid"
        const val MESSAGE_TOKEN = "messageToken"
    }

    fun addString(key: String, value: String)
    fun getString(key: String): String
    fun removeString(key: String)
    fun clearAll()
}