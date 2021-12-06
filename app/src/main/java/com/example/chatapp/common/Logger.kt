package com.example.chatapp.common

import android.content.Context
import android.util.Log

class Logger(private val context: Context) {
    companion object {
        private val INSTANCE: Logger? by lazy { null }
        private const val TAG = "Custom Log"

        fun getInstance(context: Context) = INSTANCE ?: Logger(context)
    }

    fun logInfo(message: String, tag: String = TAG) {
        val scope = context.javaClass.simpleName
        Log.i("$scope:$tag", message)
    }

    fun logError(message: String, tag: String = TAG) {
        val scope = context.javaClass.simpleName
        Log.e("$scope:$tag", message)
    }
}