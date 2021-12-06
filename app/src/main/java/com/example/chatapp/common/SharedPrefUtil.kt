package com.example.chatapp.common

import android.content.Context
import android.content.SharedPreferences

class SharedPrefUtil(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("Chat-App", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "userid"
        private val INSTANCE: SharedPrefUtil? by lazy { null }

        fun getInstance(context: Context) = INSTANCE ?: SharedPrefUtil(context)
    }

    fun addString(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()

    fun getString(key: String): String? = sharedPreferences.getString(key, null)

    fun removeString(key: String) = sharedPreferences.edit().remove(key).apply()

    fun clearAll() = sharedPreferences.edit().clear().apply()
}