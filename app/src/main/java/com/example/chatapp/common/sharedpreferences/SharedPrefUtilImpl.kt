package com.example.chatapp.common.sharedpreferences

import android.content.Context
import com.example.chatapp.common.sharedpreferences.SharedPrefUtil.Companion.USER_ID

class SharedPrefUtilImpl(context: Context) : SharedPrefUtil {
    private val sharedPreferences = context.getSharedPreferences("Chat-App", Context.MODE_PRIVATE)

    companion object {
        private val INSTANCE: SharedPrefUtilImpl? by lazy { null }

        fun getInstance(context: Context) = INSTANCE ?: SharedPrefUtilImpl(context)
    }

    override fun addString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    override fun getString(key: String): String = sharedPreferences.getString(key, "") ?: ""

    override fun removeString(key: String) = sharedPreferences.edit().remove(key).apply()

    override fun clearAll() {
        removeString(USER_ID)
    }
}